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

import com.salesforce.omakase.data.Prefix;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * Code generator for the {@link Prefix} enum.
 * <p/>
 * To modify the list of prefixes, edit the 'src/test/resources/data/prefixes.txt' file and execute the main method on this class
 * (also available via bin/run.sh).
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public final class PrefixToEnum {
    private PrefixToEnum() {}

    public static void main(String[] args) throws TemplateException, IOException {
        SourceWriter writer = new SourceWriter();

        writer.generator(PrefixToEnum.class);
        writer.classToWrite(Prefix.class);
        writer.source("prefixes.yaml");
        writer.template("prefix-to-enum.ftl");

        writer.write();
    }
}
