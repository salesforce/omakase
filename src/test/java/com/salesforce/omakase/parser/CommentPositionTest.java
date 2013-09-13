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

package com.salesforce.omakase.parser;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit test that ensures that regular comments are correctly associated with units and that orphaned comments are created as
 * appropriate.
 */
@SuppressWarnings({"JavaDoc", "ConstantConditions"})
public class CommentPositionTest {
    private static final String SRC1 = "/*x*/\n" +
        "\n" +
        "/*1*/.class /*2*/ /* 3 */.class/**4*/.class /*5**/,/*-6-*/ p#id /*!7*/ { /*\n8*/\n" +
        "    /*9*/ border: /*10*/ 1px /*11*/ solid red /*12*/;/*13*/ /*14*/\n" +
        "    /*15*/ margin: 1px; /*16*/\n" +
        "    /*17*/\n" +
        "}\n" +
        "\n" +
        "/*18*//*19*/\n";

    private static final String SRC2 = "/*x*/\n" +
        "/*x*/.class /*x*/.class/*x*/.class /*x*/,/*x*/ p#id /*x*/ { /*x*/\n" +
        "    /*x*/ margin: 1px /*x*/\n" +
        "    /*x*/\n" +
        "} /*x*/\n";

    @Test
    public void parsesUnrefined() {
        parseNoRefine(SRC1);
        parseNoRefine(SRC2);
        // no errors
    }

    @Test
    public void parsesRefined() {
        parseRefined(SRC1);
        parseRefined(SRC2);
        // no errors
    }

    @Test
    public void hasExpectedContentWhenRefined() {
        SyntaxTree tree = parseRefined(SRC1);

        // stylesheet
        Stylesheet stylesheet = tree.stylesheet();
        assertThat(stylesheet.comments()).hasSize(1);
        assertThat(Iterables.getLast(stylesheet.comments()).content()).isEqualTo("1");

        // TODO add orphaned

        // rule
        SyntaxCollection<Statement> statements = stylesheet.statements();
        assertThat(statements).hasSize(1);
        Statement statement = Iterables.getLast(statements);
        Rule rule = statement.asRule().get();

        assertThat(rule.comments()).hasSize(1);
        assertThat(Iterables.getLast(rule.comments()).content()).isEqualTo("1");

        // TODO add orphaned

        // selectors
        assertThat(rule.selectors()).hasSize(2);
        Selector selector1 = Iterables.get(rule.selectors(), 0, null);
        Selector selector2 = Iterables.get(rule.selectors(), 1, null);

        // selector 1
        assertThat(selector1.comments()).hasSize(1);
        assertThat(Iterables.getLast(selector1.comments()).content()).isEqualTo("1");
        assertThat(selector1.orphanedComments()).hasSize(1);
        assertThat(Iterables.getLast(selector1.orphanedComments()).content()).isEqualTo("5*");

        SyntaxCollection<SelectorPart> parts1 = selector1.parts();
        assertThat(parts1).hasSize(3);

        SelectorPart parts1a = Iterables.get(parts1, 0);
        SelectorPart parts1b = Iterables.get(parts1, 1);
        SelectorPart parts1c = Iterables.get(parts1, 2);

        assertThat(parts1a.comments()).hasSize(1);
        assertThat(Iterables.get(parts1a.comments(), 0).content()).isEqualTo("1");

        assertThat(parts1b.comments()).hasSize(2);
        assertThat(Iterables.get(parts1b.comments(), 0).content()).isEqualTo("2");
        assertThat(Iterables.get(parts1b.comments(), 1).content()).isEqualTo(" 3 ");

        assertThat(parts1c.comments()).hasSize(1);
        assertThat(Iterables.get(parts1b.comments(), 0).content()).isEqualTo("*4");

        // selector 2

        // declarations

        // declaration 1

        // declaration 2

    }

    private SyntaxTree parseRefined(String input) {
        SyntaxTree syntaxTree = new SyntaxTree();
        AutoRefiner refinement = new AutoRefiner().all();
        Omakase.source(input).request(refinement).request(syntaxTree).process();
        return syntaxTree;
    }

    private SyntaxTree parseNoRefine(String input) {
        SyntaxTree syntaxTree = new SyntaxTree();
        AutoRefiner refinement = new AutoRefiner().all();
        Omakase.source(input).request(refinement).request(syntaxTree).process();
        return syntaxTree;
    }
}
