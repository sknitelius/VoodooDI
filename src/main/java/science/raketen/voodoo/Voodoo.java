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

import java.lang.reflect.Method;
import java.util.Map;
import science.raketen.voodoo.context.ContextManager;
import science.raketen.voodoo.context.ContextualType;
import science.raketen.voodoo.interceptor.Interceptor;
import science.raketen.voodoo.interceptor.InterceptorBinding;
import science.raketen.voodoo.interceptor.InterceptorManager;

/**
 * Voodoo DI Container - with support for Puppet and Singleton scopes.
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class Voodoo {

    private final Map<Class, ContextualType> types;
    private final Map<InterceptorBinding, Class<? extends Interceptor>> interceptors;

    private static Voodoo voodoo;

    private Voodoo(String packageName) {
        types = ContextManager.process(packageName);
        interceptors = InterceptorManager.process(packageName);
    }

    public static Voodoo initalize() {
        return initalize("");
    }

    public static Voodoo initalize(String packageName) {
        voodoo = new Voodoo(packageName);
        return voodoo;
    }

    public static Voodoo current() {
        if (voodoo == null) {
            throw new RuntimeException("Voodoo has not been initalized.");
        }
        return voodoo;
    }

    public <T> T instance(Class<T> type) {
        return (T) types.get(type).getContextualProxy();
    }

    public Object executeInterceptorFor(Class type, Object target, Method invokedMethod, Object[] args) throws Exception {
        InterceptorBinding annotation = invokedMethod.getAnnotation(InterceptorBinding.class);
        final Class<? extends Interceptor> interceptorForMethod = interceptors.get(annotation);
        if (interceptorForMethod == null) {
            return invokedMethod.invoke(target, args);
        } else {
            Interceptor interceptor = interceptorForMethod.newInstance();
            return interceptor.invoke(type, target,invokedMethod, args);
        }
    }

}
