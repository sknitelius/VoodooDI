/*
 * Copyright 2017 Stephan Knitelius {@literal <stephan@knitelius.com>}.
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
package science.raketen.voodoo.interceptor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class InterceptorManager {

    public static Map<InterceptorBinding, Class<? extends Interceptor>> process(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forManifest()));
        Set<Class<? extends Interceptor>> interceptors = reflections.getSubTypesOf(Interceptor.class);

        Map<InterceptorBinding, Class<? extends Interceptor>> interceptorMap = new HashMap<>();

        interceptors.forEach((interceptor) -> {

            Annotation[] annotations = interceptor.getAnnotations();

            Annotation interceptorAnnotation = null;

            for (Annotation annotation : annotations) {
                if (annotation instanceof InterceptorBinding) {
                    interceptorMap.put((InterceptorBinding) annotation, interceptor);
                }
            }
        });

        return interceptorMap;
    }

}
