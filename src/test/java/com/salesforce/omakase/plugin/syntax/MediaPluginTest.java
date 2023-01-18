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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.StatementIterable;
import com.salesforce.omakase.ast.atrule.AbstractAtRuleMember;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaPluginTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    MediaPlugin plugin;
    QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        plugin = new MediaPlugin();
        broadcaster = new QueryableBroadcaster();
    }

    @Test
    public void doesntRefineExpressionIfAlreadyRefined() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"));
        TestExpression expression = new TestExpression();
        ar.expression(expression);

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(ar.expression().get()).isSameAs(expression);
    }

    @Test
    public void doesntRefineBlockIfAlreadyRefined() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"));
        TestBlock block = new TestBlock();
        ar.block(block);

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(ar.block().get()).isSameAs(block);
    }

    @Test
    public void errorsIfMissingExpression() {
        AtRule ar = new AtRule(1, 1, "media", null, new RawSyntax(2, 2, ".class{color:red}"));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MEDIA_EXPR);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfDidntFindMediaList() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, ""), new RawSyntax(2, 2, ""));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.DIDNT_FIND_MEDIA_LIST);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errirsIfUnparsableRemainderInExpression() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all$"), new RawSyntax(2, 2, ".class{color:red}"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse");
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), null);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MEDIA_BLOCK);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfUnparsableRemainderInBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}$"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse");
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void broadcastsTheExpression() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<MediaQueryList> expression = broadcaster.find(MediaQueryList.class);
        assertThat(expression.isPresent()).isTrue();
    }

    @Test
    public void broadcastsTheBlock() {
        AtRule ar = new AtRule(1, 1, "media", new RawSyntax(1, 1, "all"), new RawSyntax(2, 2, ".class{color:red}"));

        plugin.refine(ar, new Grammar(), broadcaster);
        Optional<GenericAtRuleBlock> block = broadcaster.find(GenericAtRuleBlock.class);
        assertThat(block.isPresent()).isTrue();
        assertThat(block.get().statements()).hasSize(1);
    }

    private static final class TestExpression extends AbstractAtRuleMember implements AtRuleExpression {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}

        @Override
        public TestExpression copy() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class TestBlock extends AbstractAtRuleMember implements AtRuleBlock {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}

        @Override
        public Iterator<Statement> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestBlock copy() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SyntaxCollection<StatementIterable, Statement> statements() {
            throw new UnsupportedOperationException();
        }
    }
}
