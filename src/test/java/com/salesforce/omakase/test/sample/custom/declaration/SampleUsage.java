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

package com.salesforce.omakase.test.sample.custom.declaration;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.plugin.SyntaxPlugin;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

/**
 * An example of using the sample custom declaration (and at-rule!) classes in this package.
 * <p/>
 * This feature mimics the "mixin" functionality common in CSS preprocessors. Mixins are defined in the CSS source using at-rule
 * grammar. Subsequent rules can utilize a custom declaration syntax that includes the mixin into the rule, optionally with
 * parameters. By including the mixin, the mixin's template declarations are copied into the rule.
 * <p/>
 * <b>CAVEAT:</b> custom declarations are at the edge of what's currently supported. These example classes aren't pristine, but it
 * should at least give an idea of what things are currently possible.
 * <p/>
 * We give the parser an instance of the {@link MixinPlugin}. This plugin is a {@link SyntaxPlugin} that registers our {@link
 * MixinRefiner}. The refiner handles actually parsing the custom mixin at-rules as well as the custom declaration mixin
 * references. The nature of our new syntax also necessitates creating a custom {@link MixinTokenFactory}, which we use to allow
 * declarations to begin with our special {@code +} symbol.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "JavaDoc"})
public final class SampleUsage {
    public static void main(String[] args) throws IOException {
        // sample CSS input
        String input = "" +
            "@mixin size(value) {\n" +
            "  width: $value;\n" +
            "  height: $value;\n" +
            "}\n" +
            "\n" +
            "@mixin vertical-align() {\n" +
            "  position: relative;\n" +
            "  top: 50%;\n" +
            "  transform: translateY(-50%);\n" +
            "}\n" +
            "\n" +
            ".button {\n" +
            "  font-size: 13px;\n" +
            "  +size: 10px;\n" +
            "}\n" +
            "\n" +
            ".box {\n" +
            "  background-color: #f0f0f0;\n" +
            "  +size: 25px;\n" +
            "}\n" +
            "\n" +
            ".intro {\n" +
            "  background-color: #f0f0f0;\n" +
            "  +vertical-align: true;\n" +
            "}";

        System.out.println("Sample Custom Declaration and At Rule\n");

        System.out.println("INPUT:\n--------------------");
        System.out.println(input);

        // setup the plugins we want
        StyleWriter verbose = StyleWriter.verbose();
        StandardValidation validation = new StandardValidation();
        MixinPlugin mixins = new MixinPlugin();

        // parse
        Omakase.source(input)
            .use(verbose)
            .use(validation)
            .use(mixins)
            .process();

        System.out.println("\n\nOUTPUT:\n--------------------");
        verbose.writeTo(System.out);
    }
}

