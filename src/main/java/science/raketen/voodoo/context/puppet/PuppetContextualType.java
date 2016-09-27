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
package science.raketen.voodoo.context.puppet;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import science.raketen.voodoo.context.ContextualType;

/**
 * Puppet scope - provides a new instance every time.
 * 
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class PuppetContextualType<T> extends ContextualType {

  public PuppetContextualType(Class type) {
    super(type);
  }

  @Override
  public T getContextualInstance() {
    try {
      Constructor<T> constructor = getType().getConstructor(new Class[]{});
      return constructor.newInstance(new Object[]{});
    } catch (Exception ex) {
      Logger.getLogger(PuppetContextualType.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException(ex);
    }
  }
  
}
