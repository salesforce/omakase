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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.plugin.prefixer.Prefixer;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.goldfile.Goldfile;
import com.salesforce.omakase.test.util.QuickWriter;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.File;
import java.io.IOException;

/** Temp test for debugging. */
@SuppressWarnings({"JavaDoc", "UnusedDeclaration", "SpellCheckingInspection"})
public final class Debug {
    public static final String DEBUG_FILE = "/debug/debug.css";

    public static final String STRING_SRC = "[hidden]{color: red}";

    private Debug() {}

    public static void main(String[] args) throws IOException {
        File f = new File(Goldfile.class.getResource(DEBUG_FILE).getFile());
        String fileSrc = Files.toString(f, Charsets.UTF_8);

        String source = fileSrc;

        withPlugins(source);
    }

    private static void withPlugins(String source) throws IOException {
        StyleWriter writer = StyleWriter.verbose();

        Prefixer prefixer = Prefixer.customBrowserSupport();
        prefixer.support().all(Browser.FIREFOX);
        prefixer.support().all(Browser.CHROME);
        prefixer.support().all(Browser.SAFARI);
        prefixer.support().all(Browser.IOS_SAFARI);

        Omakase.source(source)
            .use(writer)
            .use(new StandardValidation())
            .use(prefixer)
            .process();

        System.out.println();
        writer.writeTo(System.out);
    }

    private static void writeAllModes(String source) throws IOException {
        QuickWriter.writeAllModes(source);
    }
}
