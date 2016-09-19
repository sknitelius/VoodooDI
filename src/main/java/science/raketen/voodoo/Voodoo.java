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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.reflections.Reflections;

/**
 * Voodoo DI Container - selecting @Puppet annotated types only. 
 * 
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class Voodoo {

  private final ConcurrentHashMap<Class, Class> TYPES = new ConcurrentHashMap<>();

  private Voodoo() {
  }

  public static Voodoo initalize() {
    return initalize("");
  }

  public static Voodoo initalize(String packageName) {
    final Voodoo voodoo = new Voodoo();
    voodoo.scan(packageName);
    return voodoo;
  }

  private void scan(String packageName) {
    Reflections reflections = new Reflections(packageName);
    Set<Class<? extends Object>> types = reflections.getTypesAnnotatedWith(Puppet.class);
    types.stream()
            .filter((type) -> (!type.isInterface()))
            .forEach((type) -> {
              registerInterfaces(type);
              registerSuperTypes(type);
            });
  }

  private void registerSuperTypes(Class type) {
    Class<?> superclass = type.getSuperclass();
    while (superclass != Object.class) {
      TYPES.put(superclass, type);
      superclass = type.getSuperclass();
    }
  }

  private void registerInterfaces(Class type) {
    TYPES.put(type, type);
    for (Class interFace : type.getInterfaces()) {
      TYPES.put(interFace, type);
    }
  }

  public <T> T instance(Class<T> clazz) {
    T newInstance = null;
    try {
      Constructor<T> constructor = TYPES.get(clazz).getConstructor(new Class[]{});
      newInstance = constructor.newInstance(new Object[]{});
      processFields(clazz, newInstance);
    } catch (Exception ex) {
      Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return newInstance;
  }

  private <T> void processFields(Class<T> clazz, T targetInstance) {
    for (Field field : clazz.getDeclaredFields()) {
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
