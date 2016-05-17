/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.sample.custom.function;

import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.sample.custom.function.CustomVarRefiner.Mode;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.Map;

/**
 * An example of using the sample custom function classes in this package.
 * <p>
 * The custom function represents a variable lookup, using the format <code>custom-var(varName)</code>.
 * <p>
 * We give the parser an instance of the {@link CustomVarPlugin}. This plugin is a {@link SyntaxPlugin} that registers our {@link
 * CustomVarRefiner}. The refiner handles actually parsing the custom function, and creates {@link CustomVarFunction} AST objects.
 * Because we make the AST object {@link Subscribable}, it can be subcribed to like any other standard AST objects, which our
 * {@link CustomVarCounter} plugin demonstrates.
 * <p>
 * This sample usage parses a CSS source twice. The first time we just count the number of times the custom function is used, but
 * we don't resolve anything. The second time we resolve and replace the custom function with the substituted values.
 * <p>
 * Things to try:
 * <p>
 * <b>1)</b> Change the variable values. <b>2)</b> Have the sample CSS reference an invalid variable. <b>3)</b> Have a variable
 * value result in invalid CSS (e.g., making primary-color too many chars). <b>4)</b> Write another custom plugin that validates
 * Terms, and see how it validates the substituted variable values as well, etc...
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "JavaDoc"})
public final class SampleUsage {
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

        System.out.println("Sample Custom Function\n");

        System.out.println("INPUT:\n--------------------");
        System.out.println(input);
        System.out.println();

        // setup the plugins we want
        StyleWriter verbose = StyleWriter.verbose();
        StandardValidation validation = new StandardValidation();
        CustomVarPlugin passthrough = new CustomVarPlugin(Mode.PASSTHROUGH, VARS);
        CustomVarPlugin resolving = new CustomVarPlugin(Mode.RESOLVE, VARS);
        CustomVarCounter counting = new CustomVarCounter();

        // parse without resolving the vars, but count them
        Omakase.source(input)
            .use(verbose)
            .use(validation)
            .use(passthrough)
            .use(counting)
            .process();

        System.out.println("\nOUTPUT (passthrough):\n-------------------------");
        verbose.writeTo(System.out);
        counting.summarize(System.out);

        // this time resolve the vars
        Omakase.source(input)
            .use(verbose)
            .use(validation)
            .use(resolving)
            .process();

        System.out.println("\n\n\nOUTPUT (resolved):\n-------------------------");
        verbose.writeTo(System.out);

        System.out.println("\n\n");
    }
}
