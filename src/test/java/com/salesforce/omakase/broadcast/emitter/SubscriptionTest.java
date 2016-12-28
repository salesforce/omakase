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

package com.salesforce.omakase.broadcast.emitter;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.NoopBroadcaster;
import com.salesforce.omakase.error.DefaultErrorManager;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link Subscription}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SubscriptionTest {
    private ErrorManager em;

    @Before
    public void setup() {
        this.em = new DefaultErrorManager();
    }
    @Test
    public void testFilterDoesntHaveNameEventDoesntHaveName() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEvent event = new TestEvent();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testFilterDoesntHaveNameEventHasName() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamed event = new TestEventNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testFilterHasNameEventDoesntHaveName() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEvent event = new TestEvent();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isFalse();
    }

    @Test
    public void testFilterHasNameEventHasNameDoesntMatches() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamed event = new TestEventNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "edgar");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isFalse();
    }

    @Test
    public void testFilterHasNameEventHasNameMatches() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamed event = new TestEventNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testNameCaseMismatch1() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamedUpper event = new TestEventNamedUpper();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "SETSUNA");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testNameCaseMismatch2() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamedUpper event = new TestEventNamedUpper();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testNameCaseMismatch3() {
        Subscriber subscriber = new Subscriber();
        Method m = subscriber.getClass().getMethods()[0];
        TestEventNamed event = new TestEventNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "SETSUNA");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    public static final class Subscriber {
        private boolean invoked;

        public void foo(TestEvent event, Grammar grammer, Broadcaster b) {
            this.invoked = true;
        }

    }

    private static class TestEvent extends AbstractSyntax {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public Syntax copy() {
            return null;
        }
    }

    private static final class TestEventNamed extends TestEvent implements Named {
        @Override
        public String name() {
            return "setsuna";
        }
    }

    private static final class TestEventNamedUpper extends TestEvent implements Named {
        @Override
        public String name() {
            return "SETSUNA";
        }
    }
}