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

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Code generator for java files.
 * <p>
 * An optional source file can be provided to {@link #source(String)}, which will load the given file from
 * 'src/test/resources/data/'. The file given should be in YAML format. If the top-level YAML object is a map, all of the keys in
 * the map will be available in the template. If the top-level YAML object is a list, the list will be made available to the
 * template under the key "items".
 *
 * @author nmcwilliams
 */
final class SourceWriter {
    private static final Yaml yaml = new Yaml();

    private final Map<String, Object> data = Maps.newHashMap();
    private String templateName;
    private Class<?> generator;
    private Class<?> klass;
    private String source;

    /** specifies which class is responsible for generating the enum (for javadoc comment) */
    public SourceWriter generator(Class<?> generator) {
        this.generator = generator;
        return this;
    }

    /** specifies the class to rewrite */
    public SourceWriter classToWrite(Class<?> klass) {
        this.klass = klass;
        return this;
    }

    /** name of the template, with the extension, e.g., "prefix-to-enum.ftl" */
    public SourceWriter template(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /** data file to read, with extension, to be made available under an "items" list, e.g., "keywords.yaml" */
    public SourceWriter source(String source) {
        this.source = source;
        return this;
    }

    /** adds data for the template */
    public SourceWriter data(String key, Object data) {
        this.data.put(key, data);
        return this;
    }

    /**
     * performs the actual writing
     *
     * @return True if the file contents have changed.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean write() throws IOException, TemplateException {
        checkState(templateName != null, "template not set");
        checkState(generator != null, "generator not set");
        checkState(klass != null, "class to write not set");
        System.out.println(String.format("regenerating '%s'", klass));

        File file = Tools.getSourceFile(klass);
        final String original = Files.toString(file, Charsets.UTF_8);

        StringWriter writer = new StringWriter();

        // add common data
        data.put("generator", generator);
        data.put("package", klass.getPackage().getName());

        // optionally add data from specified source file
        if (source != null) {
            System.out.println(String.format("reading '%s'...", source));
            Object contents = yaml.load(Tools.readFile("/data/" + this.source));

            if (contents instanceof List) {
                Collections.sort((List)contents);
                data.put("items", contents);
            } else if (contents instanceof Map) {
                data.putAll((Map)contents);
            } else {
                throw new UnsupportedOperationException("Unable to understand data file contents. Check for valid YAML usage.");
            }
        }

        // load the template
        System.out.println("loading template...");
        Template template = Tools.getTemplate(templateName);

        // process the template with the current data
        System.out.println("processing template...");
        template.process(data, writer);

        // write it to the source file
        System.out.println("writing output...");
        Files.write(writer.toString(), file, Charsets.UTF_8);

        final String updated = Files.toString(file, Charsets.UTF_8);

        System.out.println("done\n");

        return !original.equals(updated);
    }
}
