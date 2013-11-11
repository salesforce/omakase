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

package com.salesforce.omakase.test.util.tool;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p/>
 * An optional source file can be provided to {@link #source(String)}, which will load the given file from
 * 'src/test/resources/data/'. The file given should be in YAML format. If the top-level YAML object is a map, all of the keys in
 * the map will be available in the template. If the top-level YAML object is a list, the list will be made available to the
 * template under the key "items".
 *
 * @author nmcwilliam
 */
final class SourceWriter {
    private static final Logger logger = LoggerFactory.getLogger(SourceWriter.class);
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

    /** performs the actual writing */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void write() throws IOException, TemplateException {
        checkState(templateName != null, "template not set");
        checkState(generator != null, "generator not set");
        checkState(klass != null, "class to write not set");
        logger.info("regenerating '{}'", klass);

        File file = Tools.getSourceFile(klass);
        StringWriter writer = new StringWriter();

        // add common data
        data.put("generator", generator);
        data.put("package", klass.getPackage().getName());

        // optionally add data from specified source file
        if (source != null) {
            logger.info("reading '{}'...", source);
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
        logger.info("loading template...");
        Template template = Tools.getTemplate(templateName);

        // process the template with the current data
        logger.info("processing template...");
        template.process(data, writer);

        // write it to the source file
        logger.info("writing output...");
        Files.write(writer.toString(), file, Charsets.UTF_8);

        logger.info("done\n");
    }
}
