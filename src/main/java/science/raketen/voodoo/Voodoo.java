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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import science.raketen.voodoo.context.ContextProcessor;
import science.raketen.voodoo.context.ContextualType;

/**
 * Voodoo DI Container - with support for Puppet and Singleton scopes.
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class Voodoo {

  private final Map<Class, ContextualType> types;

  private Voodoo(String packageName) {
    types = ContextProcessor.process(packageName);
  }

  public static Voodoo initalize() {
    return initalize("");
  }

  public static Voodoo initalize(String packageName) {
    return new Voodoo(packageName);
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
