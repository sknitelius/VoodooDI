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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple Type Scanner, scans direct class path of Application.
 *
 * @author Stephan Knitelius <stephan@knitelius.com>
 */
public class TypeScanner {

    public static List<Class> find(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);

            return Collections.list(resources).parallelStream()
                    .map(resource -> scanResource(resource.getFile(), packageName))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<Class> scanResource(String path, String packageName) {
        return scanResource(new File(path), packageName);
    }

    private static List<Class> scanResource(File directory, String packageName) {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                packageName = "".equals(packageName) || packageName.endsWith(".") ? packageName : packageName + ".";
                classes.addAll(scanResource(file, packageName + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    packageName = packageName.endsWith(".") ? packageName : packageName + '.';
                    classes.add(Class.forName(packageName + file.getName().replace(".class", "")));
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return classes;
    }
}
