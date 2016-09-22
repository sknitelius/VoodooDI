/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo.context;

/**
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public abstract class ContextualType<T> {
  
  private final Class<T> type;
  
  public ContextualType(Class<T> type) {
    this.type = type;
  }
    
  public abstract T getContextualInstance();
  
  public Class<T> getType() {
    return type;
  }
}
