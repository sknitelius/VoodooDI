/*
 * Copyright 2016 Stephan Knitelius.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Simple Voodoo dependency injection container.
 * Only works for concrete classes.
 * 
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class Voodoo {

  private Voodoo() {
  }

  public static Voodoo initalize() throws Exception {
    return new Voodoo();
  }

  public <T> T instance(Class<T> clazz) {
    T newInstance = null;
    try {
      Constructor<T> constructor = clazz.getConstructor(new Class[]{});
      newInstance = constructor.newInstance(new Object[]{});
      processFields(clazz, newInstance);

    } catch (Exception ex) {
      Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return newInstance;
  }

  private <T> void processFields(Class<T> clazz, T targetInstance) throws SecurityException, IllegalAccessException, IllegalArgumentException {
    for (Field field : clazz.getDeclaredFields()) {
      Inject annotation = field.getAnnotation(Inject.class);
      if (annotation != null) {
        Object instance = instance(field.getType());
        field.setAccessible(true);
        field.set(targetInstance, instance);
      }
    }
  }

}
