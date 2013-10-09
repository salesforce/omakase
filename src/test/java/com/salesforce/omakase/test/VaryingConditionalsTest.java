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

import com.google.common.collect.Sets;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.Conditionals;
import com.salesforce.omakase.plugin.validator.StandardValidation;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

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
        "  .class {color:blue}\n" +
        "  #id:hover {border:1px solid red}\n" +
        "}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "@if(ie7) {\n" +
        "  #id2:hover {border:1px solid red}\n" +
        "}\n" +
        "@if(webkit) {\n" +
        "  body {color:black}\n" +
        "}";

    private static final String EXPECTED_IE7 = ".class {color:red}\n" +
        ".class {color:blue}\n" +
        "#id:hover {border:1px solid red}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "#id2:hover {border:1px solid red}\n";

    private static final String EXPECTED_BOTH = ".class {color:red}\n" +
        ".class {color:blue}\n" +
        "#id:hover {border:1px solid red}\n" +
        "#id2:hover {border:5px solid black}\n" +
        "#id2:hover {border:1px solid red}\n" +
        "body {color:black}";

    private static final String WEBKIT_ONLY = ".class {color:red}\n" +
        "\n" +
        "#id2:hover {border:5px solid black}\n" +
        "\n" +
        "body {color:black}";

    private static final String NONE = ".class {color:red}\n" +
        "\n" +
        "#id2:hover {border:5px solid black}\n\n";

    @Test
    public void test() throws IOException {
        // setup
        Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7"));
        StandardValidation validation = new StandardValidation();
        AutoRefiner refiner = new AutoRefiner().all();
        StyleWriter inline = new StyleWriter(WriterMode.INLINE);

        // parsing
        Omakase
            .source(INPUT)
            .request(conditionals)
            .request(validation)
            .request(refiner)
            .request(inline)
            .process();

        // ie7 only
        assertThat(inline.write()).describedAs("ie7 only").isEqualTo(EXPECTED_IE7);

        // ie7 + webkit
        conditionals.manager().addTrueConditions("webkit");
        assertThat(inline.write()).describedAs("ie7 + webkit").isEqualTo(EXPECTED_BOTH);

        // webkit only
        conditionals.manager().removeTrueCondition("ie7");
        assertThat(inline.write()).describedAs("webkit only").isEqualTo(WEBKIT_ONLY);

        // ie8
        conditionals.manager().clearTrueConditions().addTrueConditions("ie8");
        assertThat(inline.write()).describedAs("ie8").isEqualTo(NONE);

        // none
        conditionals.manager().clearTrueConditions();
        assertThat(inline.write()).describedAs("ie8").isEqualTo(NONE);

        // passthrough
        conditionals.manager().passthroughMode(true);
        assertThat(inline.write()).describedAs("passthrough").isEqualTo(PASSTHROUGH);
    }

    @Test
    public void testConditionalStatementsAreBroadcasted() {
        // setup
        Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7"));
        StandardValidation validation = new StandardValidation();
        AutoRefiner refiner = new AutoRefiner().all();
        TestPlugin counter = new TestPlugin();

        // parsing
        Omakase
            .source("@if (ie7) { .test {border: 1px solid red} }")
            .request(conditionals)
            .request(validation)
            .request(refiner)
            .request(counter)
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
