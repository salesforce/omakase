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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.Lists;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Iterator;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    MediaRefiner strategy;
    QueryableBroadcaster broadcaster;
    Refiner refiner;

    @Before
    public void setup() {
        strategy = new MediaRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new Refiner(broadcaster, Lists.<RefinerStrategy>newArrayList(strategy));
    }

    @Test
    public void returnsFalseIfNotApplicable() {
        AtRule ar = new AtRule(1, 1, "blah", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isFalse();
        assertThat(ar.isRefined()).isFalse();
    }

    @Test
    public void doesntRefinedExpressionIfAlreadyRefined() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        TestExpression expression = new TestExpression();
        ar.expression(expression);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.expression().get()).isSameAs(expression);
    }

    @Test
    public void doesntRefineBlockIfAlreadyRefined() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        TestBlock block = new TestBlock();
        ar.block(block);
        strategy.refine(ar, broadcaster, refiner);
        assertThat(ar.block().get()).isSameAs(block);
    }

    @Test
    public void returnsTrueIfRefinedExpressionOnly() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        ar.block(new TestBlock());
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(ar.expression().isPresent()).isTrue();
    }

    @Test
    public void returnsTrueIfRefinedBlockOnly() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        ar.expression(new TestExpression());
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(ar.block().isPresent()).isTrue();
    }

    @Test
    public void errorsIfMissingExpression() {
        AtRule ar = new AtRule(1, 1, "media", null, new RawSyntax(2, 2, ".class{color:red}"), refiner);
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MEDIA_EXPR.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfDidntFindMediaList() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, ""), new RawSyntax(2, 2, ""), refiner);
        exception.expect(ParserException.class);
        exception.expectMessage(Message.DIDNT_FIND_MEDIA_LIST.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errirsIfUnparsableRemainderInExpression() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all$"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), null, refiner);
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MEDIA_BLOCK.message());
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void errorsIfUnparsableRemainderInBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}$"), refiner);
        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse");
        strategy.refine(ar, broadcaster, refiner);
    }

    @Test
    public void setsTheExpression() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(ar.expression().isPresent()).isTrue();
    }

    @Test
    public void broadcastsTheExpression() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(broadcaster.find(MediaQueryList.class).isPresent()).isTrue();
    }

    @Test
    public void setsTheBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(ar.block().isPresent()).isTrue();
    }

    @Test
    public void broadcastsTheBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"), refiner);
        assertThat(strategy.refine(ar, broadcaster, refiner)).isTrue();
        assertThat(broadcaster.find(com.salesforce.omakase.ast.Rule.class).isPresent()).isTrue();
    }

    private static final class TestExpression extends AbstractSyntax<AtRuleExpression> implements AtRuleExpression {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}

        @Override
        protected AtRuleExpression makeCopy(Prefix prefix, SupportMatrix support) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class TestBlock extends AbstractSyntax<StatementIterable> implements AtRuleBlock {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}

        @Override
        public Iterator<Statement> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected StatementIterable makeCopy(Prefix prefix, SupportMatrix support) {
            throw new UnsupportedOperationException();
        }
    }
}
