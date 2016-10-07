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

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import science.raketen.test.puppets.Highlander;
import science.raketen.test.puppets.Scotland;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class SingletonTest {

    @Test
    public void testSingleton() {
        Voodoo initalize = Voodoo.initalize();
        Highlander highlanderRef1 = initalize.instance(Highlander.class);

        Highlander highlanderRef2 = initalize.instance(Highlander.class);
        assertTrue(highlanderRef1 == highlanderRef2);
    }

    @Test
    public void testSingletonInjection() {
        Voodoo container = Voodoo.initalize();
        Highlander highlander = container.instance(Highlander.class);

        Scotland scotland = container.instance(Scotland.class);
        assertTrue(highlander == scotland.getHighlander());
    }
}
