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

import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.NoopBroadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SelectorPlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SelectorPluginTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void refineSelector() {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        Selector selector = new Selector(raw);

        SelectorPlugin plugin = new SelectorPlugin();
        plugin.refine(selector, new Grammar(), broadcaster);

        Iterable<SelectorPart> parts = broadcaster.filter(SelectorPart.class);
        assertThat(parts).hasSize(3);
        assertThat(Iterables.get(parts, 0)).isInstanceOf(ClassSelector.class);
        assertThat(Iterables.get(parts, 1)).isInstanceOf(Combinator.class);
        assertThat(Iterables.get(parts, 2)).isInstanceOf(IdSelector.class);
    }

    @Test
    public void refineSelectorThrowsErrorIfHasUnparsableContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id !!!!");
        Selector selector = new Selector(raw);

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNPARSABLE_SELECTOR);
        new SelectorPlugin().refine(selector, new Grammar(), new NoopBroadcaster());
    }

    @Test
    public void refinedSelectorAddsOrphanedComments() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id /*orphaned*/");
        Selector selector = new Selector(raw);

        new SelectorPlugin().refine(selector, new Grammar(), new NoopBroadcaster());
        assertThat(selector.orphanedComments()).isNotEmpty();
    }
}