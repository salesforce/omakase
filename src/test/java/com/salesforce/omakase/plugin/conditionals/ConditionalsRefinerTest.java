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

package com.salesforce.omakase.plugin.conditionals;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refinement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConditionalsRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    public static final String VALID_NAME = "if";
    private static final RawSyntax VALID_EXPRESSION = new RawSyntax(1, 1, "(ie7)");
    private static final RawSyntax VALID_BLOCK = new RawSyntax(1, 1, "  .class{color:red;\n  margin:10px;}\n\n #id1, " +
        "#id2 { padding: 0}  \n");

    private MasterRefiner refiner;
    private QueryableBroadcaster broadcaster;
    private ConditionalsRefiner strategy;

    @Before
    public void setup() {
        strategy = new ConditionalsRefiner(new ConditionalsConfig().addTrueConditions("ie7"));
        broadcaster = new QueryableBroadcaster();
        refiner = new MasterRefiner(broadcaster).register(strategy);
    }

    @Test
    public void returnsFalseForNonMatchingAtRule() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, "{}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isSameAs(Refinement.NONE);
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void ifWithoutExpressionThrowsError() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, null, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_CONDITIONAL_EXPRESSION.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void ifWithoutBlockThrowsError() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, null, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_CONDITIONAL_BLOCK.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfInvalidExpressionSyntax() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "ie7"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find opening parenthesis");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfExtraUnparsableExpressionContent() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie7)$"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content in the conditional at-rule");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfExtraUnparsableBlockContent() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, new RawSyntax(1, 1, ".class{color:red} $"), refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content in the conditional at-rule");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void whenSuccessfulReturnsTrue() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        Refinement result = strategy.refine(ar, broadcaster, refiner);
        assertThat(result).isSameAs(Refinement.FULL);
        assertThat(broadcaster.find(ConditionalAtRuleBlock.class).isPresent()).isTrue();
    }

    @Test
    public void whenSuccessfulAddsBlockToAtRule() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        assertThat(ar.block().get()).isInstanceOf(ConditionalAtRuleBlock.class);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
        assertThat(block.statements()).hasSize(2);
    }

    @Test
    public void whenSuccessfulBroadcastsTheBlock() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        Iterable<ConditionalAtRuleBlock> found = broadcaster.filter(ConditionalAtRuleBlock.class);
        assertThat(found).hasSize(1);
    }

    @Test
    public void setsAtRuleToNotPrintOutName() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.shouldWriteName()).isFalse();
    }

    @Test
    public void parsedConditionIsLowerCased() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(IE7)"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
    }

    @Test
    public void parsedConditionIsTrimmed() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(  ie7 )"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
    }

    @Test
    public void setsLineAndColumnNumbers() {
        AtRule ar = new AtRule(5, 2, VALID_NAME, new RawSyntax(5, 3, "(  ie7 )"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.line()).isEqualTo(5);
        assertThat(block.column()).isEqualTo(2);
    }

    @Test
    public void parsesNegation() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(!ie7)"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        Conditional c = block.conditionals().get(0);
        assertThat(c.condition()).isEqualTo("ie7");
        assertThat(c.isLogicalNegation()).isTrue();
    }

    @Test
    public void parsesLogicalOR() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || ie7)"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();

        Conditional c1 = block.conditionals().get(0);
        assertThat(c1.condition()).isEqualTo("ie6");
        assertThat(c1.isLogicalNegation()).isFalse();

        Conditional c2 = block.conditionals().get(1);
        assertThat(c2.condition()).isEqualTo("ie7");
        assertThat(c2.isLogicalNegation()).isFalse();
    }

    @Test
    public void parsesNegationMixedWithLogicalOR() {
        // no, this condition does not make any sense but hey
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || !IE7 || IE8 || !ie9)"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();

        Conditional c1 = block.conditionals().get(0);
        assertThat(c1.condition()).isEqualTo("ie6");
        assertThat(c1.isLogicalNegation()).isFalse();

        Conditional c2 = block.conditionals().get(1);
        assertThat(c2.condition()).isEqualTo("ie7");
        assertThat(c2.isLogicalNegation()).isTrue();

        Conditional c3 = block.conditionals().get(2);
        assertThat(c3.condition()).isEqualTo("ie8");
        assertThat(c3.isLogicalNegation()).isFalse();

        Conditional c4 = block.conditionals().get(3);
        assertThat(c4.condition()).isEqualTo("ie9");
        assertThat(c4.isLogicalNegation()).isTrue();
    }

    @Test
    public void errorsIfInvalidLogicalOR() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 | ie7)"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find |");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfMissingConditionAfterOR() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || )"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find a valid condition name");
        strategy.refine(ar, broadcaster, refiner);
    }
}
