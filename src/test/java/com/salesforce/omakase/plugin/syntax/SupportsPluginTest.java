/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.plugin.syntax;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.ConsumingBroadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SupportsPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SupportsPluginTest {

    @Test
    public void broadCastsExpression() {
        AtRule ar = new AtRule(0, 0, "supports",
            new RawSyntax(0, 0, "--color: red"), new RawSyntax(0, 0, ".foo{color: var(--color)}"));

        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        new SupportsPlugin().refine(ar, new Grammar(), broadcaster);

        GenericAtRuleExpression expression = broadcaster.find(GenericAtRuleExpression.class).get();
        assertThat(expression.expression()).isEqualTo("--color: red");
    }

    @Test
    public void broadcastsBlock() {
        AtRule ar = new AtRule(0, 0, "supports",
            new RawSyntax(0, 0, "--color: red"), new RawSyntax(0, 0, ".foo{color: var(--color)}"));

        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        new SupportsPlugin().refine(ar, new Grammar(), broadcaster);

        GenericAtRuleBlock block = broadcaster.find(GenericAtRuleBlock.class).get();
        assertThat(block.statements()).hasSize(1);
    }

    @Test
    public void testRefinement() {
        AtRule ar = new AtRule(0, 0, "supports",
            new RawSyntax(0, 0, "color: red"), new RawSyntax(0, 0, ".foo{color: red}"));

        Grammar grammar = new Grammar();
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        SelectorPlugin refineSelectors = new SelectorPlugin();
        DeclarationPlugin refineDeclarations = new DeclarationPlugin();

        broadcaster.chain(new ConsumingBroadcaster<>(
            Selector.class, s -> refineSelectors.refine(s, grammar, broadcaster)));

        broadcaster.chain(new ConsumingBroadcaster<>(
            Declaration.class, d -> refineDeclarations.refine(d, grammar, broadcaster)));

        new SupportsPlugin().refine(ar, grammar, broadcaster);

        GenericAtRuleBlock block = broadcaster.find(GenericAtRuleBlock.class).get();

        assertThat(block.statements().first().get()).describedAs("expected to find rule").isInstanceOf(Rule.class);
        Rule rule = (Rule)block.statements().first().get();

        assertThat(rule.selectors()).hasSize(1);
        assertThat(rule.selectors().first().get().parts())
            .describedAs("selector should have parts").isNotEmpty();

        assertThat(rule.declarations()).hasSize(1);
        assertThat(rule.declarations().first().get().propertyValue().members())
            .describedAs("declaration should have terms").isNotEmpty();
    }
}