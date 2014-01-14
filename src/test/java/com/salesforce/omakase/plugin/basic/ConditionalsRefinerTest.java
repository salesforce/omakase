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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.refiner.GenericRefiner;
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

    private GenericRefiner refiner;
    private QueryableBroadcaster broadcaster;
    private ConditionalsRefiner strategy;

    @Before
    public void setup() {
        strategy = new ConditionalsRefiner(new ConditionalsManager().addTrueConditions("ie7"));
        broadcaster = new QueryableBroadcaster();
        refiner = new GenericRefiner(broadcaster).register(strategy);
    }

    @Test
    public void returnsFalseForNonMatchingAtRule() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, "{}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isFalse();
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

        boolean result = strategy.refine(ar, broadcaster, refiner);
        assertThat(result).isTrue();
        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void whenSuccessfulAddsBlockToAtRule() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, VALID_EXPRESSION, VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        assertThat(ar.block().get()).isInstanceOf(ConditionalAtRuleBlock.class);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.condition()).isEqualTo("ie7");
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
        assertThat(block.condition()).isEqualTo("ie7");
    }

    @Test
    public void parsedConditionIsTrimmed() {
        AtRule ar = new AtRule(1, 1, VALID_NAME, new RawSyntax(1, 1, "(  ie7 )"), VALID_BLOCK, refiner);
        new Stylesheet(broadcaster).append(ar);

        strategy.refine(ar, broadcaster, refiner);

        ConditionalAtRuleBlock block = (ConditionalAtRuleBlock)ar.block().get();
        assertThat(block.condition()).isEqualTo("ie7");
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
}
