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

package com.salesforce.omakase.plugin.basic;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.util.Util;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SyntaxTree}.
 * <p/>
 * This contains some tests that strictly speaking aren't necessarily testing {@link SyntaxTree} itself. This is due to leftovers
 * from refactoring... but meh, doesn't hurt to keep them for now.
 */
@SuppressWarnings("JavaDoc")
public class SyntaxTreeTest {
    private static final String SRC = "@charset \"UTF8\";\n" +
        "@media all and (max-width:800px) { p{color:red}}\n" +
        "p{ color: yellow; font-size: 1em; margin: 10px;}\n" +
        "a:hover{color: green}\n" +
        "@media all and (max-width:800px) { a:hover{color:red}}\n" +
        ".class1 > .class2 {}" +
        "#div1, #div2 { position:absolute; /*orphaned-r*/}\n" +
        "/*orphaned-s*/";

    private SyntaxTree tree;
    private Stylesheet stylesheet;

    @Before
    public void setup() {
        tree = new SyntaxTree();
        Omakase.source(SRC).request(tree).process();
        stylesheet = tree.stylesheet();
    }

    @Test
    public void statements() {
        SyntaxCollection<Stylesheet, Statement> statements = stylesheet.statements();

        assertThat(statements).hasSize(7);
        assertThat(Iterables.get(statements, 0)).isInstanceOf(AtRule.class);
        assertThat(Iterables.get(statements, 1)).isInstanceOf(AtRule.class);
        assertThat(Iterables.get(statements, 2)).isInstanceOf(Rule.class);
        assertThat(Iterables.get(statements, 3)).isInstanceOf(Rule.class);
        assertThat(Iterables.get(statements, 4)).isInstanceOf(AtRule.class);
        assertThat(Iterables.get(statements, 5)).isInstanceOf(Rule.class);
        assertThat(Iterables.get(statements, 6)).isInstanceOf(Rule.class);
    }

    @Test
    public void selectorsOrder() {
        Rule rule = Iterables.get(stylesheet.statements(), 6).asRule().get();
        assertThat(rule.selectors()).hasSize(2);
        assertThat(rule.selectors().first().get().rawContent().content()).isEqualTo("#div1");
        assertThat(rule.selectors().last().get().rawContent().content()).isEqualTo("#div2");
    }

    @Test
    public void declarationsOrder() {
        Rule rule = Iterables.get(stylesheet.statements(), 2).asRule().get();
        assertThat(rule.declarations()).hasSize(3);
        assertThat(Iterables.get(rule.declarations(), 0).rawPropertyName().get().content()).isEqualTo("color");
        assertThat(Iterables.get(rule.declarations(), 1).rawPropertyName().get().content()).isEqualTo("font-size");
        assertThat(Iterables.get(rule.declarations(), 2).rawPropertyName().get().content()).isEqualTo("margin");
    }

    @Test
    public void ruleOrphanedComments() {
        Rule rule = Iterables.get(stylesheet.statements(), 6).asRule().get();
        assertThat(rule.orphanedComments()).isNotEmpty();
    }

    @Test
    public void sheetOrphanedComments() {
        assertThat(stylesheet.orphanedComments()).isNotEmpty();
    }

    @Test
    public void toStringTest() {
        assertThat(tree.toString()).isNotEqualTo(Util.originalToString(tree));
    }
}
