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

package com.salesforce.omakase.test;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.Prefixer;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.util.QuickWriter;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/** Temp test for debugging. */
@SuppressWarnings({"JavaDoc", "UnusedDeclaration", "SpellCheckingInspection"})
public final class Debug {
    public static final String SRC = ".THIS{color: red}";

    private Debug() {}

    public static void main(String[] args) throws IOException {
        withPlugins(SRC);
//        writeAllModes(SRC);
    }

    private static void withPlugins(String source) throws IOException {
        StyleWriter writer = StyleWriter.verbose();

        Prefixer prefixer = Prefixer.defaultBrowserSupport();

        Omakase.source(source)
            .use(writer)
            .use(new StandardValidation())
            .use(new Plugin() {
                @Rework
                public void addDeclaration(Rule rule) {
                    Declaration d = new Declaration(Property.COLOR, KeywordValue.of(Keyword.YELLOW));
                    rule.declarations().append(d);
                }
            })
            .process();

        System.out.println();
        writer.writeTo(System.out);
    }

    private static void writeAllModes(String source) throws IOException {
        QuickWriter.writeAllModes(source);
    }
}
