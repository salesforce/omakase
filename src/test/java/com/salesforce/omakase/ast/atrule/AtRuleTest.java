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

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AtRule}. */
@SuppressWarnings("JavaDoc")
public class AtRuleTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private RawSyntax rawExpression;
    private RawSyntax rawBlock;
    private MasterRefiner refiner;

    @Before
    public void setup() {
        rawExpression = new RawSyntax(1, 1, "all and (max-width: 800px)");
        rawBlock = new RawSyntax(1, 1, "p { color: red;}");
        refiner = new MasterRefiner(new QueryableBroadcaster());
    }

    @Test
    public void getName() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.name()).isEqualTo("media-x");
    }

    @Test
    public void getRawExpression() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.rawExpression().get()).isSameAs(rawExpression);
    }

    @Test
    public void getRawBlock() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.rawBlock().get()).isSameAs(rawBlock);
    }

    @Test
    public void expressionAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.expression().isPresent()).isFalse();
    }

    @Test
    public void blockAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.block().isPresent()).isFalse();
    }

    @Test
    public void isRefinedFalse() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void asRule() {
        Statement ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.asRule().isPresent()).isFalse();
    }

    @Test
    public void asAtRule() {
        Statement ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
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
    public void hasRefinedExpressionTrue() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomExpression expression = new CustomExpression();
        assertThat(ar.expression(expression).hasRefinedExpression()).isTrue();
    }

    @Test
    public void hasRefinedExpressionFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.hasRefinedExpression()).isFalse();
    }

    @Test
    public void setCustomBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomBlock block = new CustomBlock();
        ar.block(block);
        assertThat(ar.block().get()).isSameAs(block);
    }

    @Test
    public void hasRefinedBlockTrue() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomBlock block = new CustomBlock();
        ar.block(block);
        assertThat(ar.block(block).hasRefinedBlock()).isTrue();
    }

    @Test
    public void hasRefinedBlockFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        assertThat(ar.hasRefinedBlock()).isFalse();
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
    public void setsExpressionParentFromConstructor() {
        AtRuleExpression expression = new CustomExpression();
        assertThat(expression.parent().isPresent()).isFalse();
        AtRule ar = new AtRule("test", expression, new CustomBlock());
        assertThat(expression.parent().get()).isSameAs(ar);
    }

    @Test
    public void setsExpressionParentFromMethod() {
        AtRuleExpression expression = new CustomExpression();
        assertThat(expression.parent().isPresent()).isFalse();
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.expression(expression);
        assertThat(expression.parent().get()).isSameAs(ar);
    }

    @Test
    public void setsBlockParentFromConstructor() {
        AtRuleBlock block = new CustomBlock();
        assertThat(block.parent().isPresent()).isFalse();
        AtRule ar = new AtRule("test", new CustomExpression(), block);
        assertThat(block.parent().get()).isSameAs(ar);
    }

    @Test
    public void setsBlockParentFromMethod() {
        AtRuleBlock block = new CustomBlock();
        assertThat(block.parent().isPresent()).isFalse();
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.block(block);
        assertThat(block.parent().get()).isSameAs(ar);
    }

    @Test
    public void propagatesBroadcastToExpression() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomExpression expression = new CustomExpression();
        ar.expression(expression);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        ar.propagateBroadcast(qb);
        assertThat(qb.find(CustomExpression.class).get()).isSameAs(expression);
    }

    @Test
    public void propagatesBroadcastToBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        CustomBlock block = new CustomBlock();
        ar.block(block);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        ar.propagateBroadcast(qb);
        assertThat(qb.find(CustomBlock.class).get()).isSameAs(block);
    }

    @Test
    public void isWritableAlwaysTrueWhenNotRefined() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        assertThat(ar.isWritable()).isTrue();
    }

    @Test
    public void isWritableFalse_expressionNotWritable() {
        // false when expression present but not writable, block present and writable
        AtRule ar = new AtRule("test", new CustomExpressionNotWritable(), new CustomBlock());
        ar.shouldWriteName(true);
        assertThat(ar.isWritable()).isFalse();
    }

    @Test
    public void isWritableFalse_blockPresentNotWritable() {
        // false when block present but not writable, expression present and writable
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlockNotWritable());
        ar.shouldWriteName(true);
        assertThat(ar.isWritable()).isFalse();
    }

    @Test
    public void isWritableTrue_expressionWritableBlockAbsent() {
        // true if expression writable, block not present
        AtRule ar = new AtRule("test", new CustomExpression(), null);
        ar.shouldWriteName(true);
        assertThat(ar.isWritable()).isTrue();
    }

    @Test
    public void isWritableTrue_blockWritableExpressionNotPresent() {
        // true if block writable, expression not present
        AtRule ar = new AtRule("test", null, new CustomBlock());
        ar.shouldWriteName(true);
        assertThat(ar.isWritable()).isTrue();
    }

    @Test
    public void isWritableTrue_blockAndExpressionWritableNameTrue() {
        // true if block writable and expression writable, should write name true
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.shouldWriteName(true);
        assertThat(ar.isWritable()).isTrue();
    }

    @Test
    public void isWritableTrue_blockAndExpressionWritableNameFalse() {
        // true if block writable and expression writable, should write name false
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.shouldWriteName(false);
        assertThat(ar.isWritable()).isTrue();
    }

    @Test
    public void isWritableFalse_noneWritableWriteNameFalse() {
        // false when none writable, should write name false
        AtRule ar = new AtRule("test", new CustomExpressionNotWritable(), new CustomBlockNotWritable());
        ar.shouldWriteName(false);
        assertThat(ar.isWritable()).isFalse();
    }

    // true if should write name true, everything else absent
    // false when none present, should write name false
    // testing above conditions n/a as at least an expression or a block is currently required

    @Test
    public void writeUnrefined() throws IOException {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock, refiner);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(ar)).isEqualTo("@media all and (max-width: 800px) {\n  p { color: red;}\n}");
    }

    @Test
    public void writeUnrefinedNoBlockCharset() {
        AtRule ar = new AtRule(5, 5, "charset", new RawSyntax(5, 5, "\"UTF8\""), null, refiner);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(ar)).isEqualTo("@charset \"UTF8\";");
    }

    @Test
    public void writeUnrefinedNoBlockImport() {
        AtRule ar = new AtRule(5, 5, "import", new RawSyntax(5, 5, "url(xyz.css)"), null, refiner);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(ar)).isEqualTo("@import url(xyz.css);");
    }

    @Test
    public void writeCustomExpressionOnly() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), null);
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test (custom)");
    }

    @Test
    public void writeCustomBlockOnly() throws IOException {
        AtRule ar = new AtRule("test", null, new CustomBlock());
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test{custom}");
    }

    @Test
    public void writeCustomExpressionAndBlock() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("@test (custom){custom}");
    }

    @Test
    public void shouldWriteNameTrueByDefault() {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        assertThat(ar.shouldWriteName()).isTrue();
    }

    @Test
    public void shouldWriteNameFalse() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.shouldWriteName(false);
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("(custom){custom}");
    }

    @Test
    public void copyRefined() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("test", expression, block);

        AtRule copy = (AtRule)ar.copy();
        assertThat(copy.name()).isEqualTo("test");
        assertThat(copy.expression().get()).isInstanceOf(CustomExpression.class);
        assertThat(copy.block().get()).isInstanceOf(CustomBlock.class);
    }

    @Test
    public void copyNotRefined() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock, refiner);
        AtRule copy = (AtRule)ar.copy();

        assertThat(copy.name()).isEqualTo("media-x");
        assertThat(copy.rawExpression().isPresent()).isTrue();
        assertThat(copy.rawBlock().isPresent()).isTrue();
    }

    @Test
    public void prefix() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("keyframes", expression, block);

        SupportMatrix support = new SupportMatrix().browser(Browser.CHROME, 30);
        ar.prefix(Prefix.WEBKIT, support);
        assertThat(ar.name()).isEqualTo("-webkit-keyframes");
    }

    @Test
    public void prefixWhenNotNeeded() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("keyframes", expression, block);

        SupportMatrix support = new SupportMatrix().browser(Browser.CHROME, 30);
        ar.prefix(Prefix.MOZ, support);
        assertThat(ar.name()).isEqualTo("keyframes");
    }

    @Test
    public void markAsMetadataRule() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null, refiner);
        ar.markAsMetadataRule();
        assertThat(ar.shouldWriteName()).isFalse();
        assertThat(ar.isRefined()).isTrue();
        assertThat(ar.expression().get()).isInstanceOf(MetadataExpression.class);
        assertThat(StyleWriter.compressed().writeSnippet(ar)).isEqualTo("");
    }

    public static final class CustomExpression extends AbstractAtRuleMember implements AtRuleExpression {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("(custom)");
        }

        @Override
        public CustomExpression copy() {
            return new CustomExpression();
        }

    }

    public static final class CustomExpressionNotWritable extends AbstractAtRuleMember implements AtRuleExpression {
        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("(custom)");
        }

        @Override
        public CustomExpression copy() {
            return new CustomExpression();
        }
    }

    public static final class CustomBlock extends AbstractAtRuleMember implements AtRuleBlock {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("{custom}");
        }

        @Override
        public Iterator<Statement> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CustomBlock copy() {
            return new CustomBlock();
        }

        @Override
        public SyntaxCollection<StatementIterable, Statement> statements() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class CustomBlockNotWritable extends AbstractAtRuleMember implements AtRuleBlock {
        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            appendable.append("{custom}");
        }

        @Override
        public Iterator<Statement> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CustomBlock copy() {
            return new CustomBlock();
        }

        @Override
        public SyntaxCollection<StatementIterable, Statement> statements() {
            throw new UnsupportedOperationException();
        }
    }
}
