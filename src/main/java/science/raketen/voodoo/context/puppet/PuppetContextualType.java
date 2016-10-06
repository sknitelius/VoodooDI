/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo.context.puppet;

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
      return (T) createInstance(getType());
  }
  
}
