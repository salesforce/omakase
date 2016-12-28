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

package com.salesforce.omakase.plugin.core;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SyntaxTree}.
 * <p>
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

    private Stylesheet stylesheet;

    @Before
    public void setup() {
        SyntaxTree tree = new SyntaxTree();
        Omakase.source(SRC).use(tree).process();
        stylesheet = tree.stylesheet();
    }

    @Test
    public void statements() {
        SyntaxCollection<StatementIterable, Statement> statements = stylesheet.statements();

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
        Rule rule = (Rule)Iterables.get(stylesheet.statements(), 6);
        assertThat(rule.selectors()).hasSize(2);
        assertThat(rule.selectors().first().get().raw().get().content()).isEqualTo("#div1");
        assertThat(rule.selectors().last().get().raw().get().content()).isEqualTo("#div2");
    }

    @Test
    public void declarationsOrder() {
        Rule rule = (Rule)Iterables.get(stylesheet.statements(), 2);
        assertThat(rule.declarations()).hasSize(3);
        assertThat(Iterables.get(rule.declarations(), 0).rawPropertyName().get().content()).isEqualTo("color");
        assertThat(Iterables.get(rule.declarations(), 1).rawPropertyName().get().content()).isEqualTo("font-size");
        assertThat(Iterables.get(rule.declarations(), 2).rawPropertyName().get().content()).isEqualTo("margin");
    }

    @Test
    public void ruleOrphanedComments() {
        Rule rule = (Rule)Iterables.get(stylesheet.statements(), 6);
        assertThat(rule.orphanedComments()).isNotEmpty();
    }

    @Test
    public void sheetOrphanedComments() {
        assertThat(stylesheet.orphanedComments()).isNotEmpty();
    }
}
