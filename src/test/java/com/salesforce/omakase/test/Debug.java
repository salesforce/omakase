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
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.Prefixer;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.util.EchoLogger;
import com.salesforce.omakase.test.util.QuickWriter;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/** Temp test for debugging. */
@SuppressWarnings("JavaDoc")
public final class Debug {
    public static final String SRC = "@keyframes test {\n" +
        "  from {top: 0%}\n" +
        "  50% {top: 50%}\n" +
        "  to {top: 100%}\n" +
        "}";

    private Debug() {}

    public static void main(String[] args) throws IOException {
        StyleWriter writer = StyleWriter.verbose();

        Prefixer prefixer = Prefixer.defaultBrowserSupport();
        prefixer.prune(true);
        prefixer.rearrange(true);
        prefixer.support().browser(Browser.SAFARI, 4);
        prefixer.support().browser(Browser.FIREFOX, 3.6);
        prefixer.support().browser(Browser.CHROME, 24);

        Omakase.source(SRC)
            .request(writer)
            .request(new EchoLogger())
            .request(new StandardValidation())
            // .request(prefixer)
            .request(new Plugin() {

            })
            .process();

        System.out.println();
//        writer.writeTo(System.out);

        QuickWriter.writeAllModes(SRC);
    }
}
