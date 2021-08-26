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

package com.salesforce.omakase.broadcast;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Unit tests for {@link EmittingBroadcaster}.
 *
 * @author nmcwilliams
 */
public class EmittingBroadcasterTest {
    @Test
    public void emits() {
        EmittingBroadcaster eb = new EmittingBroadcaster();
        InnerPlugin ip = new InnerPlugin();
        eb.register(ip);
        eb.phase(SubscriptionPhase.PROCESS);
        ClassSelector cs = new ClassSelector(1, 1, "test");
        eb.broadcast(cs);
        assertThat(ip.called).isTrue();
        assertThat(cs.status()).isSameAs(Status.PROCESSED);
    }

    @Test
    public void doesntEmitForInappropriatePhase() {
        EmittingBroadcaster eb = new EmittingBroadcaster();
        InnerPlugin ip = new InnerPlugin();
        eb.register(ip);
        eb.phase(SubscriptionPhase.REFINE);
        eb.broadcast(new ClassSelector(1, 1, "test"));
        assertThat(ip.called).isFalse();
    }

    @Test
    public void relaysToInner() {
        EmittingBroadcaster eb = new EmittingBroadcaster();
        InnerBroadcaster ib = eb.chain(new InnerBroadcaster());

        eb.broadcast(new ClassSelector(1, 1, "test"));
        assertThat(ib.called).isTrue();
    }

    @Test
    public void passesGrammarAndBroadcaster() {
        Grammar grammar = new Grammar();
        Broadcaster broadcaster = new NoopBroadcaster();

        EmittingBroadcaster eb = new EmittingBroadcaster();
        eb.grammar(grammar);
        eb.root(broadcaster);

        InnerPlugin ip = new InnerPlugin();
        eb.register(ip);
        eb.phase(SubscriptionPhase.REFINE);
        eb.broadcast(new Selector(new RawSyntax(-1, -1, "foo")));
        assertThat(ip.grammar).isSameAs(grammar);
        assertThat(ip.broadcaster).isSameAs(broadcaster);
    }

    public static final class InnerPlugin implements Plugin {
        boolean called = false;
        private Grammar grammar;
        private Broadcaster broadcaster;

        @Refine
        public void refine(Selector selector, Grammar grammar, Broadcaster broadcaster) {
            this.grammar = grammar;
            this.broadcaster = broadcaster;
        }

        @Rework
        public void rework(ClassSelector selector) {
            called = true;
        }
    }

    public static final class InnerBroadcaster extends AbstractBroadcaster {
        boolean called = false;

        @Override
        public void broadcast(Broadcastable broadcastable) {
            called = true;
        }
    }
}
