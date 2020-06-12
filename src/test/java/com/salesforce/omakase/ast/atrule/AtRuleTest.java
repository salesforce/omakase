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

package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
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

    @Before
    public void setup() {
        rawExpression = new RawSyntax(1, 1, "all and (max-width: 800px)");
        rawBlock = new RawSyntax(1, 1, "p { color: red;}");
    }

    @Test
    public void getName() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.name()).isEqualTo("media-x");
    }

    @Test
    public void getRawExpression() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.rawExpression().get()).isSameAs(rawExpression);
    }

    @Test
    public void getRawBlock() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.rawBlock().get()).isSameAs(rawBlock);
    }

    @Test
    public void expressionAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.expression().isPresent()).isFalse();
    }

    @Test
    public void blockAbsentByDefault() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.block().isPresent()).isFalse();
    }

    @Test
    public void isRefinedFalse() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        assertThat(ar.isRefined()).isFalse();
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
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        CustomExpression expression = new CustomExpression();
        ar.expression(expression);
        assertThat(ar.expression().get()).isSameAs(expression);
    }

    @Test
    public void hasRefinedExpressionTrue() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        CustomExpression expression = new CustomExpression();
        assertThat(ar.expression(expression).expression().isPresent()).isTrue();
    }

    @Test
    public void hasRefinedExpressionFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        assertThat(ar.expression().isPresent()).isFalse();
    }

    @Test
    public void setCustomBlock() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        CustomBlock block = new CustomBlock();
        ar.block(block);
        assertThat(ar.block().get()).isSameAs(block);
    }

    @Test
    public void hasRefinedBlockTrue() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        CustomBlock block = new CustomBlock();
        ar.block(block);
        assertThat(ar.block(block).block().isPresent()).isTrue();
    }

    @Test
    public void hasRefinedBlockFalse() {
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        assertThat(ar.block().isPresent()).isFalse();
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

        exception.expect(IllegalArgumentException.class);
        ar.expression(null);
    }

    @Test
    public void setsExpressionParentFromConstructor() {
        AtRuleExpression expression = new CustomExpression();
        assertThat(expression.parent()).isNull();
        AtRule ar = new AtRule("test", expression, new CustomBlock());
        assertThat(expression.parent()).isSameAs(ar);
    }

    @Test
    public void setsExpressionParentFromMethod() {
        AtRuleExpression expression = new CustomExpression();
        assertThat(expression.parent()).isNull();
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.expression(expression);
        assertThat(expression.parent()).isSameAs(ar);
    }

    @Test
    public void setsBlockParentFromConstructor() {
        AtRuleBlock block = new CustomBlock();
        assertThat(block.parent()).isNull();
        AtRule ar = new AtRule("test", new CustomExpression(), block);
        assertThat(block.parent()).isSameAs(ar);
    }

    @Test
    public void setsBlockParentFromMethod() {
        AtRuleBlock block = new CustomBlock();
        assertThat(block.parent()).isNull();
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        ar.block(block);
        assertThat(block.parent()).isSameAs(ar);
    }

    @Test
    public void propagatesBroadcastToExpressionAndBlock() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();

        AtRule ar = new AtRule("test", expression, block);

        QueryableBroadcaster qb = new QueryableBroadcaster();
        ar.propagateBroadcast(qb, Status.PARSED);
        assertThat(qb.find(CustomExpression.class).get()).isSameAs(expression);
        assertThat(qb.find(CustomBlock.class).get()).isSameAs(block);
    }

    @Test
    public void isWritableAlwaysTrueWhenNotRefined() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
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
        AtRule ar = new AtRule(5, 5, "media", rawExpression, rawBlock);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(ar)).isEqualTo("@media all and (max-width: 800px) {\n  p { color: red;}\n}");
    }

    @Test
    public void writeUnrefinedNoBlockCharset() {
        AtRule ar = new AtRule(5, 5, "charset", new RawSyntax(5, 5, "\"UTF8\""), null);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(ar)).isEqualTo("@charset \"UTF8\";");
    }

    @Test
    public void writeUnrefinedNoBlockImport() {
        AtRule ar = new AtRule(5, 5, "import", new RawSyntax(5, 5, "url(xyz.css)"), null);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(ar)).isEqualTo("@import url(xyz.css);");
    }

    @Test
    public void writeCustomExpressionOnly() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), null);
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("@test (custom)");
    }

    @Test
    public void writeCustomBlockOnly() throws IOException {
        AtRule ar = new AtRule("test", null, new CustomBlock());
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("@test{custom}");
    }

    @Test
    public void writeCustomExpressionAndBlock() throws IOException {
        AtRule ar = new AtRule("test", new CustomExpression(), new CustomBlock());
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("@test (custom){custom}");
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
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("(custom){custom}");
    }

    @Test
    public void copyRefined() {
        CustomExpression expression = new CustomExpression();
        CustomBlock block = new CustomBlock();
        AtRule ar = new AtRule("test", expression, block);

        AtRule copy = ar.copy();
        assertThat(copy.name()).isEqualTo("test");
        assertThat(copy.expression().get()).isInstanceOf(CustomExpression.class);
        assertThat(copy.block().get()).isInstanceOf(CustomBlock.class);
    }

    @Test
    public void copyNotRefined() {
        AtRule ar = new AtRule(5, 5, "media-x", rawExpression, rawBlock);
        AtRule copy = ar.copy();

        assertThat(copy.name()).isEqualTo("media-x");
        assertThat(copy.rawExpression().isPresent()).isTrue();
        assertThat(copy.rawBlock().isPresent()).isTrue();
    }

    @Test
    public void markAsMetadataRule() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null);
        ar.markAsMetadataRule();
        assertThat(ar.shouldWriteName()).isFalse();
        assertThat(ar.isRefined()).isTrue();
        assertThat(ar.expression().get()).isInstanceOf(MetadataExpression.class);
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("");
    }

    @Test
    public void markAsMetadataRuleDoesntReplaceExistingExpression() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null);
        CustomExpressionNotWritable expr = new CustomExpressionNotWritable();
        ar.expression(expr);
        ar.markAsMetadataRule();
        assertThat(ar.shouldWriteName()).isFalse();
        assertThat(ar.isRefined()).isTrue();
        assertThat(ar.expression().get()).isSameAs(expr);
        assertThat(StyleWriter.compressed().writeSingle(ar)).isEqualTo("");
    }

    @Test
    public void breakBroadcastIfNeverEmit() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null);
        ar.status(Status.NEVER_EMIT);
        assertThat(ar.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void breakBroadcastIfAlreadyRefined() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null);
        ar.expression(new CustomExpression());
        assertThat(ar.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void dontBreakBroadcastIfNotRefined() {
        AtRule ar = new AtRule(1, 1, "meta", new RawSyntax(1, 1, "ahoy"), null);
        assertThat(ar.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isFalse();
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
