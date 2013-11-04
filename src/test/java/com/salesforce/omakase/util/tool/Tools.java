/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.util.tool;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * File utilities, mainly for the generators.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class Tools {
    private static final Configuration FREEMARKER = new Configuration();

    static {
        FREEMARKER.setClassForTemplateLoading(Tools.class, "/templates");
    }

    private Tools() {}

    /** reads a file from the classpath */
    public static String readFile(String path) throws IOException {
        return Files.toString(getFile(path), Charsets.UTF_8);
    }

    /** finds a file from the classpath */
    public static File getFile(String path) {
        return new File(Tools.class.getResource(path).getFile());
    }

    /** finds the source file for the given class */
    public static File getSourceFile(Class<?> klass) {
        URL url = klass.getResource(klass.getSimpleName() + ".class");
        return new File(url.getFile().replace("target/classes", "src/main/java").replace(".class", ".java"));
    }

    /** finds a template on the classpath */
    public static Template getTemplate(String name) throws IOException {
        return FREEMARKER.getTemplate(String.format("%s.ftl", name));
    }
}
