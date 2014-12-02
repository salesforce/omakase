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

package com.salesforce.omakase.test.sample.customfunction;

import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.test.sample.customfunction.SampleCustomFunctionRefiner.Mode;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Map;

/**
 * An example of using our sample custom function classes.
 * <p/>
 * The custom function represents a variable lookup, using the format <code>custom-var(varName)</code>.
 * <p/>
 * This sample usages parses a CSS source twice. The first time we just count the number of times the custom function is used, but
 * we don't resolve anything. The second time we resolve and replace the custom function with the substituted values.
 * <p/>
 * Things to try:
 * <p/>
 * <b>1)</b> Change the variable values. <b>2)</b> Have the sample CSS reference an invalid variable. <b>3)</b> Have a variable
 * value result in invalid CSS (e.g., making primary-color too many chars). <b>4)</b> Write another custom plugin that validates Terms, and see how it validates the
 * substituted variable values as well, etc...
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"SpellCheckingInspection", "JavaDoc", "UtilityClassWithoutPrivateConstructor"})
public final class SampleCustomFunctionUsage {
    // the sample variables
    private static final Map<String, String> VARS = ImmutableMap.<String, String>builder()
        .put("primary-color", "#56ff00")
        .put("font-family", "Arial, 'Helvetica Neue', Helvetica, sans-serif")
        .build();

    public static void main(String[] args) throws IOException {
        // sample CSS input
        String input = "" +
            ".button {\n" +
            "  font-size: 13px;\n" +
            "  background: #f2f2f2;\n" +
            "  color: custom-var(primary-color);\n" +
            "  font-family: custom-var(font-family);\n" +
            "}\n" +
            "\n" +
            ".panel {\n" +
            "  background: custom-var(primary-color)\n" +
            "}";

        // setup the plugins we want
        StyleWriter verbose = StyleWriter.verbose();
        StandardValidation validation = new StandardValidation();
        SampleCustomFunctionPlugin passthrough = new SampleCustomFunctionPlugin(Mode.PASSTHROUGH, VARS);
        SampleCustomFunctionPlugin resolve = new SampleCustomFunctionPlugin(Mode.RESOLVE, VARS);
        SampleCustomFunctionCounter counting = new SampleCustomFunctionCounter();

        // parse without resolving the vars, but count them
        Omakase.source(input)
            .request(verbose)
            .request(validation)
            .request(passthrough)
            .request(counting)
            .process();

        System.out.printf("OUTPUT (passthrough):\n");
        verbose.writeTo(System.out);
        counting.summarize(System.out);

        // this time resolve the vars
        Omakase.source(input)
            .request(verbose)
            .request(validation)
            .request(resolve)
            .process();

        System.out.printf("\n\nOUTPUT (resolved):\n");
        verbose.writeTo(System.out);

        System.out.println("\n\n");
    }
}
