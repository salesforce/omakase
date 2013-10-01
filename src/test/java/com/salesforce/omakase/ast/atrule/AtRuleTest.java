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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AtRule}. */
@SuppressWarnings("JavaDoc")
public class AtRuleTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private RawSyntax rawExpression;
    private RawSyntax rawBlock;
    private Refiner refiner;

    @Before
    public void setup() {
        rawExpression = new RawSyntax(1, 1, "all and (max-width: 800px)");
        rawBlock = new RawSyntax(1, 1, "p { color: red;}");
        refiner = new Refiner(new QueryableBroadcaster());
    }

    @Test
    public void getName() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.name()).isEqualTo("media");
    }

    @Test
    public void getRawExpression() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.rawExpression().get()).isSameAs(rawExpression);
    }

    @Test
    public void getRawBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.rawBlock().get()).isSameAs(rawBlock);
    }

    @Test
    public void expressionAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.expression().isPresent()).isFalse();
    }

    @Test
    public void blockAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.block().isPresent()).isFalse();
    }

    @Test
    public void isRefinedFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void asRule() {
        Statement ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.asRule().isPresent()).isFalse();
    }

    @Test
    public void asAtRule() {
        Statement ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.asAtRule().isPresent()).isTrue();
    }

    @Test
    public void customExpressionOnly() {
        CustomExpression expression = new CustomExpression();
        AtRule ar = new AtRule("test", expression, null);
        assertThat(ar.expression().get()).isSameAs(expression);
        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void customBlockOnly() {
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("test", null, block);
        assertThat(ar.block().get()).isSameAs(block);
        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void customExpressionAndBlock() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("test", expression, block);
        assertThat(ar.expression().get()).isSameAs(expression);
        assertThat(ar.block().get()).isSameAs(block);
        assertThat(ar.isRefined()).isTrue();
    }

    @Test
    public void setCustomExpression() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomExpression expression = new CustomExpression();
        ar.expression(expression);
        assertThat(ar.expression().get()).isSameAs(expression);
    }

    @Test
    public void setCustomBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomBlock block = new CustomBlock();
        ar.block(block);
        assertThat(ar.block().get()).isSameAs(block);
    }

    @Test
    public void cannotSetNullExpressionAndBlock() {
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("test", null, block);

        exception.expect(IllegalStateException.class);
        ar.block(null);
    }

    @Test
    public void cannotSetNullExpressionAndBlock2() {
        CustomExpression expression = new CustomExpression();
        AtRule ar = new AtRule("test", expression, null);

        exception.expect(IllegalStateException.class);
        ar.expression(null);
    }

    @Test
    public void writeUnrefined() throws IOException {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(ar)).isEqualTo("@media all and (max-width: 800px) {\n  p { color: red;}\n}");
    }

    @Test
    public void writeCustomExpressionOnly() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), null);
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test(custom)");
    }

    @Test
    public void writeCustomBlockOnly() throws IOException {
        AtRule ar = new AtRule("test", null, new CustomBlock());
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test{custom}");
    }

    @Test
    public void writeCustomExpressionAndBlock() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test(custom){custom}");
    }

    @Test
    public void shouldWriteNameFalse() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.shouldWriteName(false);
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("(custom){custom}");
    }

    public static final class CustomExpression extends AbstractSyntax implements AtRuleExpression {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("(custom)");
        }
    }

    public static final class CustomBlock extends AbstractSyntax implements AtRuleBlock {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("{custom}");
        }
    }

}
