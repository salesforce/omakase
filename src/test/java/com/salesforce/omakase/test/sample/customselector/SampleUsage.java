/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.customselector;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "JavaDoc"})
public final class SampleUsage {
    public static void main(String[] args) throws IOException {
        // sample CSS input
        String input = "" +
            "%button {\n" +
            "  padding: 8px 10px;\n" +
            "  border-radius: 3px;\n" +
            "  text-align: center;\n" +
            "  font-family: Helvetica, sans-serif;\n" +
            "}\n" +
            "\n" +
            ".primary|button {\n" +
            "  color: #a76ff1;\n" +
            "}\n" +
            "\n" +
            ".secondary|button {\n" +
            "  color: #6f9ff1;\n" +
            "}";

        // setup the plugins we want
        StyleWriter verbose = StyleWriter.verbose();
        StandardValidation validation = new StandardValidation();
        PlaceholderSelectorPlugin placeholders = new PlaceholderSelectorPlugin();
        PlaceholderTokenFactory placeholderTokens = new PlaceholderTokenFactory();

        // parse without resolving the vars, but count them
        Omakase.source(input)
            .request(verbose)
            .request(validation)
            .request(placeholders)
            .request(placeholderTokens)
            .process();

        verbose.writeTo(System.out);
    }
}
