/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo.context.puppet;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import science.raketen.voodoo.context.ContextualType;

/**
 * Puppet scope - provides a new instance every time.
 * 
 * @author Stephan Knitelius <stephan@knitelius.com>
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
