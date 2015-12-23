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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Property;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Declarations}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class DeclarationsTest {
    @Test
    public void testWithinStylesheetRecurseTrue() {
        Declaration d1 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d2 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d3 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d4 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d5 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d6 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d7 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d8 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));

        Rule r1 = new Rule();
        r1.selectors().append(new Selector(new ClassSelector("test")));
        r1.declarations().append(d1);
        r1.declarations().append(d2);

        MediaQueryList exp = new MediaQueryList();
        exp.queries().append(new MediaQuery().type("tv"));
        Rule arr1 = new Rule();
        arr1.selectors().append(new Selector(new ClassSelector("test")));
        arr1.declarations().append(d3);
        arr1.declarations().append(d4);
        arr1.declarations().append(d5);
        AtRuleBlock block = new GenericAtRuleBlock();
        block.statements().append(arr1);
        AtRule ar1 = new AtRule("media", exp, block);

        Rule r2 = new Rule();
        r2.selectors().append(new Selector(new ClassSelector("test")));
        r2.declarations().append(d6);
        r2.declarations().append(d7);
        r2.declarations().append(d8);

        Stylesheet stylesheet = new Stylesheet();
        stylesheet.statements().append(r1).append(ar1).append(r2);

        assertThat(Declarations.within(stylesheet, true)).containsExactly(d1, d2, d3, d4, d5, d6, d7, d8);
    }

    @Test
    public void testWithinStylesheetRecurseFalse() {
        Declaration d1 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d2 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d3 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d4 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d5 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d6 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d7 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d8 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));

        Rule r1 = new Rule();
        r1.selectors().append(new Selector(new ClassSelector("test")));
        r1.declarations().append(d1);
        r1.declarations().append(d2);

        MediaQueryList exp = new MediaQueryList();
        exp.queries().append(new MediaQuery().type("tv"));
        Rule arr1 = new Rule();
        arr1.selectors().append(new Selector(new ClassSelector("test")));
        arr1.declarations().append(d3);
        arr1.declarations().append(d4);
        arr1.declarations().append(d5);
        AtRuleBlock block = new GenericAtRuleBlock();
        block.statements().append(arr1);
        AtRule ar1 = new AtRule("media", exp, block);

        Rule r2 = new Rule();
        r2.selectors().append(new Selector(new ClassSelector("test")));
        r2.declarations().append(d6);
        r2.declarations().append(d7);
        r2.declarations().append(d8);

        Stylesheet stylesheet = new Stylesheet();
        stylesheet.statements().append(r1).append(ar1).append(r2);

        assertThat(Declarations.within(stylesheet, false)).containsExactly(d1, d2, d6, d7, d8);
    }

    @Test
    public void testWithinAtRuleRecurseFalse() {
        Declaration d1 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d2 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));
        Declaration d3 = new Declaration(Property.COLOR, KeywordValue.of(Keyword.RED));

        MediaQueryList exp = new MediaQueryList();
        exp.queries().append(new MediaQuery().type("tv"));
        Rule r1 = new Rule();
        r1.selectors().append(new Selector(new ClassSelector("test")));
        r1.declarations().append(d1);
        r1.declarations().append(d2);
        r1.declarations().append(d3);
        AtRuleBlock block = new GenericAtRuleBlock();
        block.statements().append(r1);

        assertThat(Declarations.within(block, false)).containsExactly(d1, d2, d3);
    }
}
