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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import science.raketen.voodoo.Voodoo;

/**
 * Used by scopes to provide access to the contextual instance.
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public abstract class ContextualType<T> {

    private static final Object[] EMPTY_OBJ_ARRAY = new Object[]{};
    private static final Class[] EMPTY_TYPE_ARRAY = new Class[]{};

    private final Class<T> type;

    public ContextualType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    protected T createInstance(Class<T> type) {
        try {
            T targetInstance = construct(type);
            processFields(type, targetInstance);
            processPostConstruct(type, targetInstance);
            return targetInstance;
        } catch (Exception ex) {
            Logger.getLogger(ContextualType.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private T construct(Class<T> type) throws Exception {
        List<Constructor<?>> injectableConstructors = Arrays.stream(type.getConstructors())
                .filter(constructor -> constructor.getAnnotation(Inject.class) != null)
                .collect(Collectors.toList());

        Constructor<T> constructor = null;
        Object[] params = EMPTY_OBJ_ARRAY;

        switch (injectableConstructors.size()) {
            case 0:
                //Find default constructor.
                constructor = type.getConstructor(EMPTY_TYPE_ARRAY);
                break;
            case 1:
                constructor = (Constructor<T>) injectableConstructors.get(0);
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                params = Arrays.stream(parameterTypes)
                        .map(paramType -> Voodoo.current().instance(paramType))
                        .toArray();
                break;
            default:
                throw new RuntimeException("Ambigious Injectable constructor for " + type);
        }
        return constructor.newInstance(params);
    }

    private <T> void processFields(Class<T> type, T targetInstance) {
        for (Field field : type.getDeclaredFields()) {
            Inject annotation = field.getAnnotation(Inject.class);
            if (annotation != null) {
                Object instance = Voodoo.current().instance(field.getType());
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

    private <T> void processPostConstruct(Class type, T instance) {
        Method[] declaredMethods = type.getDeclaredMethods();

        Arrays.stream(declaredMethods)
                .filter(method -> method.getAnnotation(PostConstruct.class) != null)
                .forEach(postConstructMethod -> {
                    try {
                        postConstructMethod.setAccessible(true);
                        postConstructMethod.invoke(instance, new Object[]{});
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex);
                    }
                });
    }

    public abstract T getContextualInstance();
}
