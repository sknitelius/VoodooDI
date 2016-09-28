/*
 * Copyright 2016 Stephan Knitelius {@literal <stephan@knitelius.com>}.
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class VoodooService {
  
  @Inject
  private Houngan houngan;
  
  private String interaction;
  
  public VoodooService() {
    System.out.println("Houngan: " + houngan);
  }
  
  @PostConstruct
  public void postConstruct() {
    this.interaction = houngan.summon("postConstruct");
  }

  public String getInteraction() {
    return interaction;
  }
  
}
