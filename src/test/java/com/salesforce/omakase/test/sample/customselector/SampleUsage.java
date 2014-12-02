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
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An example of using the sample custom selector classes.
 * <p/>
 * This feature mimics the "placeholder" selector functionality common in CSS preprocessors. Placeholder selectors are written
 * with a special syntax using the format <code>%name</code>. The placeholder has a rule and declarations as normal, however the
 * placeholder is not written out by default.
 * <p/>
 * Subsequent selectors can "extend" the placeholder. By extending, the placeholder will include that selector in the output.
 * Essentially this is a way of being DRY. Run the example to make this description clearer.
 * <p/>
 * We give the parser an instance of the {@link PlaceholderSelectorPlugin}. This plugin is a {@link SyntaxPlugin} that registers
 * our {@link PlaceholderSelectorRefiner}. The refiner handles actually parsing the custom selectors, and creates {@link
 * PlaceholderSelector} AST objects. The nature of our new syntax also necessitates creating a custom {@link
 * PlaceholderTokenFactory}, which we use to allow selectors to begin with our special {@code %} symbol.
 * <p/>
 * Things to try:
 * <p/>
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

        // parse without resolving the vars, but count them
        Omakase.source(input)
            .use(verbose)
            .use(validation)
            .use(placeholders)
            .process();

        System.out.println("\n\nOUTPUT:\n--------------------");
        verbose.writeTo(System.out);
    }
}
