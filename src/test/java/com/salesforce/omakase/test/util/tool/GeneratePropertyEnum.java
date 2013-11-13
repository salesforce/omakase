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

import com.salesforce.omakase.data.Property;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * Code generator for the {@link Property} enum.
 * <p/>
 * To modify the list of keywords, edit the 'src/test/resources/data/properties.yaml' file and execute the main method on this
 * class (also available via script/omakase.sh).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class GeneratePropertyEnum {
    public static void main(String[] args) throws IOException, TemplateException {
        new GeneratePropertyEnum().run();
    }

    public void run() throws IOException, TemplateException {
        SourceWriter writer = new SourceWriter();

        writer.generator(GeneratePropertyEnum.class);
        writer.classToWrite(Property.class);
        writer.source("properties.yaml");
        writer.template("property-to-enum.ftl");

        writer.write();
    }
}
