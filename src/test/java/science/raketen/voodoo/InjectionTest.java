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

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import science.raketen.test.puppets.Houngan;

/**
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class InjectionTest {

  @Test
  public void testInterfaceInjection() {
    Voodoo container = Voodoo.initalize();
    Houngan helloService = container.instance(Houngan.class);
    assertTrue(helloService.summon("Hogo").contains("Hogo"));
  }
  
}
