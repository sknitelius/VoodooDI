/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo.context.singleton;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import science.raketen.voodoo.context.ContextualType;

/**
 * Manages the singleton contextual instance.
 * 
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
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
