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
package science.raketen.test.puppets;

import java.lang.reflect.Method;
import science.raketen.voodoo.interceptor.Interceptor;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
@Logger
public class LogInterceptor implements Interceptor {

    @Override
    public Object invoke(Class type, Object target, Method invokedMethod, Object[] args) throws Exception {
        System.out.println(String.format("Called %s", invokedMethod.getName()));
        Object returnValue = invokedMethod.invoke(target, args);
        System.out.println(String.format("Method %s returned %s", invokedMethod.getName(),returnValue));
        return returnValue;
    }
    
}
