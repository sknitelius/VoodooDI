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
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Voodoo DI Container - capable of handling Interface and SuperType injection.
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class Voodoo {

  /**
   * Maps the relevant concrete implementation to the target type.
   */
  private final Map<Class, Class> types = new ConcurrentHashMap<>();

  private Voodoo() {
  }

  /**
   * Scans all files found on Classpath.
   *
   * @return Voodoo - Voodoo container instance.
   */
  public static Voodoo initalize() {
    final Voodoo voodoo = new Voodoo();
    voodoo.scan("");
    return initalize("");
  }

  /**
   * Limits scanning to the specified package.
   *
   * @param packageName - name of package to be scanned.
   * @return Voodoo - Voodoo container instance.
   */
  public static Voodoo initalize(String packageName) {
    final Voodoo voodoo = new Voodoo();
    voodoo.scan(packageName);
    return voodoo;
  }

  private void scan(String pakageName) {
    List<Class> discoveredTypes = TypeScanner.find(pakageName);
    discoveredTypes.stream()
            .filter(type -> (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())))
            .forEach(type -> {
              registerInterfaces(type);
              registerSuperTypes(type);
            });
  }

  private void registerSuperTypes(Class type) {
    Class<?> supertype = type.getSuperclass();
    while (type != null && type != Object.class) {
      if (types.containsKey(supertype)) {
        throw new RuntimeException("Ambigious Puppet for " + supertype);
      }
      types.put(supertype, type);
      type = type.getSuperclass() == type ? null : type.getSuperclass();
    }
  }

  private void registerInterfaces(Class type) {
    types.put(type, type);
    for (Class interFace : type.getInterfaces()) {
      if (types.containsKey(interFace)) {
        throw new RuntimeException("Ambigious Puppet for " + interFace);
      }
      types.put(interFace, type);
    }
  }

  public <T> T instance(Class<T> type) {
    T newInstance = null;
    try {
      Constructor<T> constructor = types.get(type).getConstructor(new Class[]{});
      newInstance = constructor.newInstance(new Object[]{});
      processFields(type, newInstance);
    } catch (Exception ex) {
      Logger.getLogger(Voodoo.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
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
