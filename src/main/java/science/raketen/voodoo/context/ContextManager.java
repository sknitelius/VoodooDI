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
package science.raketen.voodoo.context;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import science.raketen.voodoo.Voodoo;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class ContextManager {

    private ContextManager() {
    }

    public static Map<Class, ContextualType> process(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forManifest()));
        Set<Class<? extends Context>> contexts = reflections.getSubTypesOf(Context.class);

        Stream<ContextualType> contextualTypes = contexts.parallelStream()
                .map(contextType -> constructContext(contextType))
                .map(context -> initalizeContext(context, reflections))
                .flatMap(ctypes -> ctypes.stream());

        return mapTypes(contextualTypes);
    }

    private static Set<ContextualType> initalizeContext(Context context, Reflections reflections) {
        Set annotatedTypes = reflections.getTypesAnnotatedWith(context.getContextAnnotation());
        return context.initalizeContext(annotatedTypes);
    }

    private static Map<Class, ContextualType> mapTypes(Stream<ContextualType> contextualTypes) {
        final Map<Class, ContextualType> types = new ConcurrentHashMap<>();
        contextualTypes.forEach(contextualType -> {
            types.put(contextualType.getType(), contextualType);
            registerInterfaces(contextualType, types);
            registerSuperTypes(contextualType, types);
        });
        return types;
    }

    private static void registerSuperTypes(ContextualType contextualType, Map<Class, ContextualType> types) {
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

    private static void registerInterfaces(ContextualType context, Map<Class, ContextualType> types) {
        Class type = context.getType();
        for (Class interFace : type.getInterfaces()) {
            if (types.containsKey(interFace)) {
                throw new RuntimeException("Ambigious Puppet for " + interFace);
            }
            types.put(interFace, context);
        }
    }

    private static Context constructContext(Class<? extends Context> contextType) throws RuntimeException {
        try {
            return contextType.getConstructor(new Class[]{}).newInstance(new Object[]{});
        } catch (Exception ex) {
            Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
