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
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.GenericAtRuleBlock;
import com.salesforce.omakase.ast.atrule.GenericAtRuleExpression;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeyframesPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class KeyframesPluginTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    KeyframesPlugin plugin;
    QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        plugin = new KeyframesPlugin();
        broadcaster = new QueryableBroadcaster();
    }

    @Test
    public void doesntBroadcastAnythingIfNotApplicable() {
        AtRule ar = new AtRule(1, 1, "blah",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"));

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(broadcaster.count()).isEqualTo(0);
    }

    @Test
    public void doesntBroadcastAnythingIfNotApplicableAndPrefixed() {
        AtRule ar = new AtRule(1, 1, "-webkit-blah",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"));

        plugin.refine(ar, new Grammar(), broadcaster);
        assertThat(broadcaster.count()).isEqualTo(0);
    }

    @Test
    public void matchesNormal() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"));

        plugin.refine(ar, new Grammar(), broadcaster);

        Optional<GenericAtRuleExpression> expression = broadcaster.find(GenericAtRuleExpression.class);
        assertThat(expression.isPresent()).isTrue();
        assertThat(expression.get().expression()).isEqualTo("test");

        Optional<GenericAtRuleBlock> block = broadcaster.find(GenericAtRuleBlock.class);
        assertThat(block.isPresent()).isTrue();
        assertThat(block.get().statements()).hasSize(1);
    }

    @Test
    public void matchesPrefixed() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px}"));

        plugin.refine(ar, new Grammar(), broadcaster);

        Optional<GenericAtRuleExpression> expression = broadcaster.find(GenericAtRuleExpression.class);
        assertThat(expression.isPresent()).isTrue();
        assertThat(expression.get().expression()).isEqualTo("test");

        Optional<GenericAtRuleBlock> block = broadcaster.find(GenericAtRuleBlock.class);
        assertThat(block.isPresent()).isTrue();
        assertThat(block.get().statements()).hasSize(1);
    }

    @Test
    public void errorsIfMissingName() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            null, new RawSyntax(2, 2, "50%{top:100px}"));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.KEYFRAME_NAME);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfInvalidName() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "$1"), new RawSyntax(2, 2, "50%{top:100px}"));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.KEYFRAME_NAME);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfUnparsableContentInExpression() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test test2"), new RawSyntax(2, 2, "50%{top:100px}"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected content");
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfMissingBlock() {
        AtRule ar = new AtRule(1, 1, "-webkit-keyframes",
            new RawSyntax(1, 1, "test"), null);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_KEYFRAMES_BLOCK);
        plugin.refine(ar, new Grammar(), broadcaster);
    }

    @Test
    public void errorsIfUnparsableContentAtEndOfBlock() {
        AtRule ar = new AtRule(1, 1, "keyframes",
            new RawSyntax(1, 1, "test"), new RawSyntax(2, 2, "50%{top:100px} $"));

        exception.expect(ParserException.class);
        exception.expectMessage("Unable to parse the remaining content");
        plugin.refine(ar, new Grammar(), broadcaster);
    }
}
