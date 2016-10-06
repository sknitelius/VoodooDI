/*
 * Copyright 2016 Stephan Knitelius <stephan@knitelius.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package science.raketen.voodoo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import science.raketen.voodoo.context.ContextualType;
import science.raketen.voodoo.context.puppet.PuppetContextualType;
import science.raketen.voodoo.context.singleton.SingletonContextualType;

/**
 * Voodoo DI Container - with support for Puppet and Singleton scopes.
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class Voodoo {

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[]{};
    private static final Class[] EMPTY_TYPE_ARRAY = new Class[]{};

    private final Map<Class, ContextualType> types = new ConcurrentHashMap<>();

    private static Voodoo voodoo;
    
    private Voodoo() {
    }

    public static Voodoo initalize() {
        return initalize("");
    }

    public static Voodoo initalize(String packageName) {
        voodoo = new Voodoo();
        voodoo.scan(packageName);
        return voodoo;
    }

    public static Voodoo current() {
        return voodoo;
    }

    private void scan(String packageName) {
        List<Class> discoveredTypes = TypeScanner.find(packageName);
        discoveredTypes.stream()
                .filter((type) -> (!type.isInterface()
                && !Modifier.isAbstract(type.getModifiers())
                && !type.getPackage().getName().startsWith("science.raketen.voodoo"))
                ).forEach((Class type) -> {
                    System.out.println(type.getPackage().getName());
                    ContextualType contextualType = buildContextualInstance(type);
                    types.put(type, contextualType);
                    registerInterfaces(contextualType);
                    registerSuperTypes(contextualType);
                });
    }

    private ContextualType buildContextualInstance(Class type) {
        Annotation singleton = type.getAnnotation(Singleton.class);
        if (singleton == null) {
            return new PuppetContextualType(type);
        } else {
            return new SingletonContextualType(type);
        }
    }

    private void registerSuperTypes(ContextualType contextualType) {
        Class type = contextualType.getType();
        Class<?> superclass = type.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            if (types.containsKey(superclass)) {
                throw new RuntimeException("Ambigious Puppet for " + superclass);
            }
            types.put(superclass, contextualType);
            superclass = type.getSuperclass() == superclass ? null : type.getSuperclass();
        }
    }

    private void registerInterfaces(ContextualType context) {
        Class type = context.getType();
        for (Class interFace : type.getInterfaces()) {
            if (types.containsKey(interFace)) {
                throw new RuntimeException("Ambigious Puppet for " + interFace);
            }
            types.put(interFace, context);
        }
    }

    public <T> T instance(Class<T> type) {
        T contextualInstance = null;
        try {
            contextualInstance = (T) types.get(type).getContextualInstance();
            processFields(type, contextualInstance);
        } catch (Exception ex) {
            Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contextualInstance;
    }

    private <T> void processFields(Class<T> type, T targetInstance) {
        for (Field field : type.getDeclaredFields()) {
            Inject annotation = field.getAnnotation(Inject.class);
            if (annotation != null) {
                Object instance = instance(field.getType());
                field.setAccessible(true);
                try {
                    field.set(targetInstance, instance);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
