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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import science.raketen.voodoo.context.Context;
import science.raketen.voodoo.context.ContextualType;

/**
 *
 * @author Stephan Knitelius {@literal <stephan@knitelius.com>}
 */
public class PuppetContext implements Context {

  private static final Class SCOPE_ANNOTATION = Puppet.class;
  
  @Override
  public Class getContextualAnnotation() {
    return SCOPE_ANNOTATION;
  }

  @Override
  public List<? extends ContextualType> initalizeContext(Reflections reflections) {
    Set<Class<?>> puppetTypes = reflections.getTypesAnnotatedWith(SCOPE_ANNOTATION);
    return puppetTypes.stream()
            .filter(type -> (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())))
            .map(type -> new PuppetContextualType(type)).collect(Collectors.toList());
  }

}
