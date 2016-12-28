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
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.Conditional;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
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

    private Grammar grammar;
    private QueryableBroadcaster broadcaster;
    private ConditionalsRefiner refiner;

    @Before
    public void setup() {
        grammar = new Grammar();
        broadcaster = new QueryableBroadcaster();
        refiner = new ConditionalsRefiner(new ConditionalsConfig().addTrueConditions("ie7"));
    }

    @Test
    public void ifWithoutExpressionThrowsError() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, null, VALID_BLOCK);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_CONDITIONAL_EXPRESSION);
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void ifWithoutBlockThrowsError() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, null);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_CONDITIONAL_BLOCK);
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void errorsIfInvalidExpressionSyntax() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "ie7"), VALID_BLOCK);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find opening parenthesis");
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void errorsIfExtraUnparsableExpressionContent() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie7)$"), VALID_BLOCK);

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content in the conditional at-rule");
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void errorsIfExtraUnparsableBlockContent() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, new RawSyntax(1, 1, ".class{color:red} $"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content in the conditional at-rule");
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void whenSuccessfulNoError() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);
        assertThat(broadcaster.find(ConditionalAtRuleBlock.class).isPresent()).isTrue();
    }

    @Test
    public void whenSuccessfulAddsConditionsAndStatements() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
        assertThat(block.statements()).hasSize(2);
    }

    @Test
    public void setsAtRuleToNotPrintOutName() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);
        assertThat(ar.shouldWriteName()).isFalse();
    }

    @Test
    public void parsedConditionIsLowerCased() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(IE7)"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
    }

    @Test
    public void parsedConditionIsTrimmed() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(  ie7 )"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();
        assertThat(block.conditionals().get(0).condition()).isEqualTo("ie7");
    }

    @Test
    public void setsLineAndColumnNumbers() {
        AtRule ar = new AtRule(5, 2, VALID_NAME, new RawSyntax(5, 3, "(  ie7 )"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();
        assertThat(block.line()).isEqualTo(5);
        assertThat(block.column()).isEqualTo(2);
    }

    @Test
    public void parsesNegation() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(!ie7)"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();
        Conditional c = block.conditionals().get(0);
        assertThat(c.condition()).isEqualTo("ie7");
        assertThat(c.isLogicalNegation()).isTrue();
    }

    @Test
    public void parsesLogicalOR() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || ie7)"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();

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
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || !IE7 || IE8 || !ie9)"), VALID_BLOCK);

        refiner.refine(ar, grammar, broadcaster);

        ConditionalAtRuleBlock block = broadcaster.find(ConditionalAtRuleBlock.class).get();

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
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 | ie7)"), VALID_BLOCK);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find |");
        refiner.refine(ar, grammar, broadcaster);
    }

    @Test
    public void errorsIfMissingConditionAfterOR() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(ie6 || )"), VALID_BLOCK);

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find a valid condition name");
        refiner.refine(ar, grammar, broadcaster);
    }
}
