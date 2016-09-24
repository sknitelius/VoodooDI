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
package science.raketen.voodoo.context.singleton;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import science.raketen.voodoo.context.ContextualType;

/**
 * Singleton Scope - creates a singleton instance of the given type.
 * 
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class SingletonContextualType<T> extends ContextualType {

  private final T singleton;
  
  public SingletonContextualType(Class type) {
    super(type);
    singleton = createSingleton(type);
  }

  private T createSingleton(Class type) {
    try {
      Constructor<T> constructor = type.getConstructor(new Class[]{});
      return constructor.newInstance(new Object[]{});
    } catch (Exception ex) {
      Logger.getLogger(SingletonContextualType.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public T getContextualInstance() {
    return singleton;
  }
  
}
