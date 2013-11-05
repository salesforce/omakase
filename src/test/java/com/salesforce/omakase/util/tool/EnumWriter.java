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
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Code generator for enums.
 *
 * @author nmcwilliam
 */
@SuppressWarnings("JavaDoc")
final class EnumWriter {
    private static final Logger logger = LoggerFactory.getLogger(EnumWriter.class);

    private final Map<String, Object> data = Maps.newHashMap();
    private Class<? extends Enum<?>> enumClass;
    private String templateName;
    private Class<?> generator;
    private String source;

    /** specifies which class is responsible for generating the enum (for javadoc comment) */
    public EnumWriter generator(Class<?> generator) {
        this.generator = generator;
        return this;
    }

    /** specifies the enum to rewrite */
    public EnumWriter enumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    /** name of the template, with the extension, e.g., "prefix-to-enum.ftl" */
    public EnumWriter template(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /** data file to read, with extension, to be made available under an "items" list, e.g., "keywords.txt" */
    public EnumWriter source(String source) {
        this.source = source;
        return this;
    }

    /** adds data for the template */
    public EnumWriter data(String key, Object data) {
        this.data.put(key, data);
        return this;
    }

    /** performs the actual writing */
    public void write() throws IOException, TemplateException {
        checkState(templateName != null, "template not set");
        checkState(generator != null, "generator not set");
        checkState(enumClass != null, "enumClass not set");
        logger.info("regenerating '{}'", enumClass);

        File file = Tools.getSourceFile(enumClass);
        StringWriter writer = new StringWriter();

        // add common data
        data.put("generator", generator);
        data.put("package", enumClass.getPackage().getName());

        // optionally add data from specified source file
        if (source != null) {
            logger.info("reading '{}'...", source);
            String source = Tools.readFile("/data/" + this.source);

            // using set to make unique
            Set<String> items = Sets.newHashSet(Splitter.on('\n').omitEmptyStrings().trimResults().split(source));
            List<String> ordered = Lists.newArrayList(items);
            Collections.sort(ordered);
            data.put("items", ordered);
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
