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
import com.salesforce.omakase.data.Property;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Utility to take list of css names and fromStrings them to the {@link Property} enum. */
@SuppressWarnings("JavaDoc")
public final class PropertyToEnum {
    private PropertyToEnum() {}

    public static void main(String[] args) throws IOException, TemplateException {
        // read the input file
        System.out.println("reading 'properties.txt'...");
        String source = Tools.readFile("/data/properties.txt");

        // reformat into an ordered list
        System.out.println("generating output...");

        // using set to make unique
        Set<String> keywords = Sets.newHashSet(Splitter.on('\n').omitEmptyStrings().trimResults().split(source));

        // convert to an order list
        List<String> ordered = Lists.newArrayList(keywords);
        Collections.sort(ordered);

        // process the template
        Template template = Tools.getTemplate("property-to-enum");
        Map<String, Object> data = Maps.newHashMap();
        data.put("properties", ordered);
        data.put("generatorName", PropertyToEnum.class);
        data.put("package", Property.class.getPackage().getName());

        StringWriter writer = new StringWriter();
        template.process(data, writer);

        // write it to the source file
        System.out.println("writing output...");
        File file = Tools.getSourceFile(Property.class);
        file.delete();
        Files.write(writer.toString(), file, Charsets.UTF_8);

        System.out.println("done");
    }
}
