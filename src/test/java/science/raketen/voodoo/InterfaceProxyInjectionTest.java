/*
 * Copyright 2017 Stephan Knitelius {@literal <stephan@knitelius.com>}.
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

import javassist.util.proxy.ProxyFactory;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import science.raketen.test.puppets.Houngan;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class InterfaceProxyInjectionTest {

    @Test
    public void testAllManagedBeansAreProxies() {
        Voodoo container = Voodoo.initalize();
        Houngan houngan = container.instance(Houngan.class);
        
        //Expecting all managed beans to be proxies.
        assertTrue(ProxyFactory.isProxyClass(houngan.getClass()));
        assertTrue(ProxyFactory.isProxyClass(houngan.getSpirit().getClass()));
        
        assertTrue(houngan.summon("Hogo").contains("Hogo"));
    }

}
