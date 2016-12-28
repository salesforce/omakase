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

package com.salesforce.omakase.sample.custom.selector;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.GrammarPlugin;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An example of using the sample custom selector classes in this package.
 * <p>
 * This feature mimics the "placeholder" selector functionality common in CSS preprocessors. Placeholder selectors are written
 * with a special syntax using the format <code>%name</code>. The placeholder has a rule and declarations as normal, however the
 * placeholder is not written out by default.
 * <p>
 * Subsequent selectors can "extend" the placeholder. By extending, the placeholder will include that selector in the output.
 * Essentially this is a way of being DRY. Run the example to make this description clearer.
 * <p>
 * We give the parser an instance of the {@link PlaceholderSelectorPlugin}. This plugin is a {@link GrammarPlugin} that registers
 * our {@link PlaceholderTokenFactory}. This plugin also handles parsing the custom selectors, and creates {@link
 * PlaceholderSelector} AST objects. The nature of our new syntax also necessitates creating a custom {@link
 * PlaceholderTokenFactory}, which we use to allow selectors to begin with our special {@code %} symbol.
 * <p>
 * Things to try:
 * <p>
 * <b>1)</b> Add more placeholders and CSS to utilize it. <b>2)</b> Try adding or making the existing placeholder unused <b>3)</b>
 * Try referencing an invalid placeholder name. <b>4)</b> Try changing <code>.primary</code> to something more complex like
 * <code>.primary > .btn </code>
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

        System.out.println("Sample Custom Selector\n");

        System.out.println("INPUT:\n--------------------");
        System.out.println(input);

        // setup the plugins we want
        StyleWriter verbose = StyleWriter.verbose();
        StandardValidation validation = new StandardValidation();
        PlaceholderSelectorPlugin placeholders = new PlaceholderSelectorPlugin();

        // parse
        Omakase.source(input)
            .use(placeholders)
            .use(verbose)
            .use(validation)
            .process();

        System.out.println("\n\nOUTPUT:\n--------------------");
        verbose.writeTo(System.out);
    }
}
