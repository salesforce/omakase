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

package com.salesforce.omakase.test.functional;

import com.google.common.collect.Sets;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.AutoRefine;
import com.salesforce.omakase.plugin.conditionals.Conditionals;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests a broader usage of {@link Conditionals}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class VaryingConditionalsTest {
    private static final String INPUT = ".class {color: red}\n" +
        "\n" +
        "@if (IE7) {\n" +
        "  .class {color: blue}\n" +
        "  #id:hover {border: 1px solid red}\n" +
        "}\n" +
        "\n" +
        "#id2:hover {border: 5px solid black}\n" +
        "\n" +
        "@if (ie7) {\n" +
        "  #id2:hover {border: 1px solid red}\n" +
        "}\n" +
        "\n" +
        "@if (webkit) {\n" +
        "  body {color: black}\n" +
        "}";

    private static final String PASSTHROUGH = ".class {color:red}\n" +
        "@if(ie7) {\n" +
        ".class {color:blue}\n" +
        "#id:hover {border:1px solid red}\n" +
        "}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "@if(ie7) {\n" +
        "#id2:hover {border:1px solid red}\n" +
        "}\n" +
        "@if(webkit) {\n" +
        "body {color:black}\n" +
        "}";

    private static final String EXPECTED_IE7 = ".class {color:red}\n" +
        ".class {color:blue}\n" +
        "#id:hover {border:1px solid red}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "#id2:hover {border:1px solid red}";

    private static final String EXPECTED_BOTH = ".class {color:red}\n" +
        ".class {color:blue}\n" +
        "#id:hover {border:1px solid red}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "#id2:hover {border:1px solid red}\n" +
        "body {color:black}";

    private static final String WEBKIT_ONLY = ".class {color:red}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "body {color:black}";

    private static final String NONE = ".class {color:red}\n" +
        "#id2:hover {border:5px solid black}";

    @Test
    public void test() throws IOException {
        // setup
        Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7"));
        StandardValidation validation = new StandardValidation();
        AutoRefine refiner = AutoRefine.everything();
        StyleWriter inline = new StyleWriter(WriterMode.INLINE);

        // parsing
        Omakase
            .source(INPUT)
            .use(refiner)
            .use(conditionals)
            .use(validation)
            .use(inline)
            .process();

        // ie7 only
        assertThat(inline.write()).describedAs("ie7 only").isEqualTo(EXPECTED_IE7);

        // ie7 + webkit
        conditionals.config().addTrueConditions("webkit");
        assertThat(inline.write()).describedAs("ie7 + webkit").isEqualTo(EXPECTED_BOTH);

        // webkit only
        conditionals.config().removeTrueCondition("ie7");
        assertThat(inline.write()).describedAs("webkit only").isEqualTo(WEBKIT_ONLY);

        // ie8
        conditionals.config().clearTrueConditions().addTrueConditions("ie8");
        assertThat(inline.write()).describedAs("ie8").isEqualTo(NONE);

        // none
        conditionals.config().clearTrueConditions();
        assertThat(inline.write()).describedAs("ie8").isEqualTo(NONE);

        // passthrough
        conditionals.config().passthroughMode(true);
        assertThat(inline.write()).describedAs("passthrough").isEqualTo(PASSTHROUGH);
    }

    @Test
    public void testConditionalStatementsAreBroadcasted() {
        // setup
        Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7"));
        StandardValidation validation = new StandardValidation();
        AutoRefine refiner = AutoRefine.everything();
        TestPlugin counter = new TestPlugin();

        // parsing
        Omakase
            .source("@if (ie7) { .test {border: 1px solid red} }")
            .use(refiner)
            .use(conditionals)
            .use(validation)
            .use(counter)
            .process();

        assertThat(counter.ruleCount).isGreaterThan(0);
        assertThat(counter.selectorCount).isGreaterThan(0);
        assertThat(counter.declarationCount).isGreaterThan(0);
        assertThat(counter.classSelectorCount).isGreaterThan(0);
    }

    @SuppressWarnings("UnusedParameters")
    public static final class TestPlugin implements Plugin {
        int ruleCount;
        int selectorCount;
        int declarationCount;
        int classSelectorCount;

        @Observe
        public void rule(Rule rule) {
            ruleCount++;
        }

        @Observe
        public void selector(Selector selector) {
            selectorCount++;
        }

        @Observe
        public void declaration(Declaration declaration) {
            declarationCount++;
        }

        @Observe
        public void classSel(ClassSelector selector) {
            classSelectorCount++;
        }
    }
}
