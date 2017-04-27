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

import java.util.Map;
import science.raketen.voodoo.context.ContextManager;
import science.raketen.voodoo.context.ContextualType;

/**
 * Voodoo DI Container - with support for Puppet and Singleton scopes.
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class Voodoo {

    private final Map<Class, ContextualType> types;

    private static Voodoo voodoo;

    private Voodoo(String packageName) {
        types = ContextManager.process(packageName);
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
        if(type.isInterface()) {
            return (T) types.get(type).getContextualProxy(type);
        } else {
            return (T) types.get(type).getContextualInstance();
        }
    }

}
