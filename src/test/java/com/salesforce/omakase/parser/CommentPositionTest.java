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

package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.plugin.core.AutoRefine;
import com.salesforce.omakase.plugin.core.SyntaxTree;

/**
 * Unit test that ensures that regular comments are correctly associated with units and that orphaned comments are created as
 * appropriate.
 */
public class CommentPositionTest {
    private static final String SRC = "/*0*/\n" +
        "\n" +
        "/*1*/.class /*2*/ /* 3 */.class/**4*/.class /*5**/,/*-6-*/ p#id /*!7*/ { /*\n8*/\n" +
        "    /*9*/ border: /*10*/ 1px/*11*/ solid red /*12*/;/*13*/ /*14*/\n" +
        "    /*15*/ margin: 1px; padding: 1px /*16*/2px; /*17*/\n" +
        "    /*18*/\n" +
        "}\n" +
        "\n" +
        "/*19*//*20*/\n";

    private Stylesheet stylesheet;
    private Rule rule;

    @Before
    public void setup() {
        SyntaxTree tree = new SyntaxTree();
        AutoRefine refine = new AutoRefine();
        Omakase.source(SRC).use(refine).use(tree).process();
        this.stylesheet = tree.stylesheet();
        this.rule = stylesheet.rules().get(0);
    }

    @Test
    public void stylesheetHasExpectedComments() {
        assertThat(stylesheet.comments()).isEmpty();
    }

    @Test
    public void stylesheetHasExpectedOrphanedComments() {
        assertThat(stylesheet.orphanedComments()).hasSize(2);
        assertThat(Iterables.get(stylesheet.orphanedComments(), 0).content()).isEqualTo("19");
        assertThat(Iterables.get(stylesheet.orphanedComments(), 1).content()).isEqualTo("20");
    }

    @Test
    public void ruleHasExpectedComments() {
        assertThat(rule.comments()).isEmpty();
    }

    @Test
    public void ruleHasExpectedOrphanedComments() {
        assertThat(rule.orphanedComments()).hasSize(2);
        assertThat(Iterables.get(rule.orphanedComments(), 0).content()).isEqualTo("17");
        assertThat(Iterables.get(rule.orphanedComments(), 1).content()).isEqualTo("18");
    }

    @Test
    public void expectedNumSelectors() {
        assertThat(rule.selectors()).hasSize(2);
    }

    @Test
    public void selector1HasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        assertThat(selector.comments()).hasSize(2);
        assertThat(Iterables.get(selector.comments(), 0).content()).isEqualTo("0");
        assertThat(Iterables.get(selector.comments(), 1).content()).isEqualTo("1");
    }

    @Test
    public void selector1HasExpectedOrphanedComments() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        assertThat(selector.orphanedComments()).hasSize(1);
        assertThat(Iterables.get(selector.orphanedComments(), 0).content()).isEqualTo("5*");
    }

    @Test
    public void selector1HasExpectedNumParts() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        assertThat(selector.parts()).hasSize(4); //including the combinator
    }

    @Test
    public void selector1FirstPartHasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        SelectorPart part = Iterables.get(selector.parts(), 0);

        assertThat(part.comments()).hasSize(0);
        //assertThat(Iterables.get(part.comments(), 0).content()).isEqualTo("0");
        //assertThat(Iterables.get(part.comments(), 1).content()).isEqualTo("1");
    }

    @Test
    public void selector1SecondPartHasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        SelectorPart part = Iterables.get(selector.parts(), 2);

        assertThat(part.comments()).hasSize(2);
        assertThat(Iterables.get(part.comments(), 0).content()).isEqualTo("2");
        assertThat(Iterables.get(part.comments(), 1).content()).isEqualTo(" 3 ");
    }

    @Test
    public void selector1ThirdPartHasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 0);
        SelectorPart part = Iterables.get(selector.parts(), 3);

        assertThat(part.comments()).hasSize(1);
        assertThat(Iterables.get(part.comments(), 0).content()).isEqualTo("*4");
    }

    @Test
    public void selector2HasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 1);
        assertThat(selector.comments()).hasSize(1);
        assertThat(Iterables.get(selector.comments(), 0).content()).isEqualTo("-6-");
    }

    @Test
    public void selector2HasExpectedOrphanedComments() {
        Selector selector = Iterables.get(rule.selectors(), 1);
        assertThat(selector.orphanedComments()).hasSize(1);
        assertThat(Iterables.get(selector.orphanedComments(), 0).content()).isEqualTo("!7");
    }

    @Test
    public void selector2HasExpectedNumParts() {
        Selector selector = Iterables.get(rule.selectors(), 1);
        assertThat(selector.parts()).hasSize(2);
    }

    @Test
    public void selector2FirstPartHasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 1);
        SelectorPart part = Iterables.get(selector.parts(), 0);

        assertThat(part.comments()).hasSize(0);
        //assertThat(Iterables.get(part.comments(), 0).content()).isEqualTo("-6-");
    }

    @Test
    public void selector2SecondPartHasExpectedComments() {
        Selector selector = Iterables.get(rule.selectors(), 1);
        SelectorPart part = Iterables.get(selector.parts(), 1);

        assertThat(part.comments()).isEmpty();
    }

    @Test
    public void expectedNumDeclarations() {
        assertThat(rule.declarations()).hasSize(3);
    }

    @Test
    public void declaration1HasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        assertThat(declaration.comments()).hasSize(2);
        assertThat(Iterables.get(declaration.comments(), 0).content()).isEqualTo("\n8");
        assertThat(Iterables.get(declaration.comments(), 1).content()).isEqualTo("9");
    }

    @Test
    public void declaration1HasExpectedOrphanedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        assertThat(declaration.orphanedComments()).hasSize(1);
        assertThat(Iterables.get(declaration.orphanedComments(), 0).content()).isEqualTo("12");
    }

    @Test
    public void declaration1HasExpectedNumTermMembers() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        assertThat(declaration.propertyValue().terms()).hasSize(3);
    }

    @Test
    public void declaration1FirstTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        Term term = Iterables.get(declaration.propertyValue().terms(), 0);

        assertThat(term.comments()).hasSize(1);
        assertThat(Iterables.get(term.comments(), 0).content()).isEqualTo("10");
    }

    @Test
    public void declaration1SecondTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        Term term = Iterables.get(declaration.propertyValue().terms(), 1);

        assertThat(term.comments()).hasSize(1);
        assertThat(Iterables.get(term.comments(), 0).content()).isEqualTo("11");
    }

    @Test
    public void declaration1ThirdTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 0);
        Term term = Iterables.get(declaration.propertyValue().terms(), 2);

        assertThat(term.comments()).isEmpty();
    }

    @Test
    public void declaration2HasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 1);
        assertThat(declaration.comments()).hasSize(3);
        assertThat(Iterables.get(declaration.comments(), 0).content()).isEqualTo("13");
        assertThat(Iterables.get(declaration.comments(), 1).content()).isEqualTo("14");
        assertThat(Iterables.get(declaration.comments(), 2).content()).isEqualTo("15");
    }

    @Test
    public void declaration2HasExpectedOrphanedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 1);
        assertThat(declaration.orphanedComments()).isEmpty();
    }

    @Test
    public void declaration2HasExpectedNumTermMembers() {
        Declaration declaration = Iterables.get(rule.declarations(), 1);
        assertThat(declaration.propertyValue().terms()).hasSize(1);
    }

    @Test
    public void declaration2FirstTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 1);
        Term term = Iterables.get(declaration.propertyValue().terms(), 0);

        assertThat(term.comments()).isEmpty();
    }

    @Test
    public void declaration3HasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 2);
        assertThat(declaration.comments()).isEmpty();
    }

    @Test
    public void declaration3HasExpectedOrphanedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 2);
        assertThat(declaration.orphanedComments()).isEmpty();
    }

    @Test
    public void declaration3HasExpectedNumTermMembers() {
        Declaration declaration = Iterables.get(rule.declarations(), 2);
        assertThat(declaration.propertyValue().terms()).hasSize(2);
    }

    @Test
    public void declaration3FirstTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 2);
        Term term = Iterables.get(declaration.propertyValue().terms(), 0);

        assertThat(term.comments()).isEmpty();
    }

    @Test
    public void declaration3SecondTermHasExpectedComments() {
        Declaration declaration = Iterables.get(rule.declarations(), 2);
        Term term = Iterables.get(declaration.propertyValue().terms(), 1);

        assertThat(term.comments()).hasSize(1);
        assertThat(Iterables.get(term.comments(), 0).content()).isEqualTo("16");
    }
}
