/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package science.raketen.voodoo;

import javax.inject.Singleton;

/**
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
@Singleton
public class God {
  public String getObjId() {
    return this.toString();
  }
}
