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

package com.salesforce.omakase.plugin.basic;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AutoRefiner}. */
@SuppressWarnings("JavaDoc")
public class AutoRefinerTest {
    private AtRule atRule;
    private Selector selector;
    private Declaration declaration;
    private AutoRefiner autoRefiner;

    @Before
    public void setup() {
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster());
        atRule = new AtRule(5, 5, "media", new RawSyntax(1, 1, "all"), new RawSyntax(1, 1, "p {color:red}"), refiner);
        selector = new Selector(new RawSyntax(1, 1, ".class"), refiner);
        declaration = new Declaration(new RawSyntax(1, 1, "color"), new RawSyntax(1, 1, "red"), refiner);
        autoRefiner = new AutoRefiner();
    }

    @Test
    public void selectorsOnly() {
        autoRefiner.selectors();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isFalse();
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isFalse();
    }

    @Test
    public void declarationsOnly() {
        autoRefiner.declarations();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isFalse();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isTrue();
    }

    @Test

    public void atRulesOnly() {
        autoRefiner.atRules();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isTrue();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isFalse();
    }

    @Test
    public void all() {
        autoRefiner.all();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isTrue();
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isTrue();
    }
}
