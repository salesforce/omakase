/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * File utilities, mainly for the generators.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({})
public final class Tools {
    private static final Configuration FREEMARKER = new Configuration();

    static {
        FREEMARKER.setClassForTemplateLoading(Tools.class, "/templates");
    }

    private Tools() {}

    /** reads a file from the classpath */
    @SuppressWarnings("deprecation")
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
        if (!name.endsWith(".ftl")) name += ".ftl";
        return FREEMARKER.getTemplate(name);
    }
}
