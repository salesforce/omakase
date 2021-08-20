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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.NoopBroadcaster;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link Subscription}.
 *
 * @author nmcwilliams
 */
public class SubscriptionTest {
    @SuppressWarnings("deprecation")
    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    private TestErrorManager em;

    public static final class TestErrorManager implements ErrorManager {
        boolean reported;

        @Override
        public void report(ParserException exception) {
            reported = true;
        }

        @Override
        public void report(ErrorLevel level, Syntax cause, String message) {
            reported = true;
        }

        @Override
        public void report(SubscriptionException exception) {
            reported = true;
        }

        @Override
        public String getSourceName() {
            return null;
        }

        @Override
        public boolean hasErrors() {
            return false;
        }

        @Override
        public boolean autoSummarize() {
            return false;
        }

        @Override
        public String summarize() {
            return null;
        }
    }

    public static class HasRefineMethod {
        public static final String refineMethodName = "refine";
        public static final Class<?>[] refineMethodArgs = new Class<?>[]{
            TestRefinable.class, Grammar.class, Broadcaster.class};

        private boolean invoked;

        public void refine(TestRefinable event, Grammar grammer, Broadcaster b) {
            this.invoked = true;
        }
    }

    private static class TestRefinable extends AbstractSyntax {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public Syntax copy() {
            return null;
        }
    }

    private static final class TestRefinableNamed extends TestRefinable implements Named {
        @Override
        public String name() {
            return "setsuna";
        }
    }

    private static final class TestRefinableNamedUpper extends TestRefinable implements Named {
        @Override
        public String name() {
            return "SETSUNA";
        }
    }

    @Before
    public void setup() {
        this.em = new TestErrorManager();
    }

    @Test
    public void testFilterDoesntHaveName_EventDoesntHaveName() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testFilterDoesntHaveName_EventHasName() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamed event = new TestRefinableNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testFilterHasName_EventDoesntHaveName() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isFalse();
    }

    @Test
    public void testFilterHasName_EventHasName_DoesntMatch() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamed event = new TestRefinableNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "edgar");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isFalse();
    }

    @Test
    public void testFilterHasName_EventHasName_Matches() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamed event = new TestRefinableNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testUpperCaseSubcriptionMatchesUpperCaseEvent() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamedUpper event = new TestRefinableNamedUpper();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "SETSUNA");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testLowerCaseSubscriptionMatchesUpperCaseEvent() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamedUpper event = new TestRefinableNamedUpper();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "setsuna");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testUpperCaseSubscriptionMatchesLowerCaseEvent() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinableNamed event = new TestRefinableNamed();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, "SETSUNA");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(subscriber.invoked).isTrue();
    }

    @Test
    public void testRefineMethodWithWrongArgSignatureThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void refine(ClassSelector selector) {}
        };
        Method m = subscriber.getClass().getMethod("refine", ClassSelector.class);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("does not have expected parameters");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
    }

    @Test
    public void testRefineMethodWithPrivateAccessThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            private void refine(TestRefinable event, Grammar grammer, Broadcaster b) {}
        };
        Method m = subscriber.getClass().getDeclaredMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("method is not accessible");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
    }

    @Test
    public void testRefineMethod_ParserException_reportsToErrorManager() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void refine(TestRefinable event, Grammar grammer, Broadcaster b) {
                throw new ParserException(event, "foo");
            }
        };

        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(em.reported).isTrue();
    }

    @Test
    public void testRefineMethod_SubscriptionException_reportsToErrorManager() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void refine(TestRefinable event, Grammar grammer, Broadcaster b) {
                throw new SubscriptionException("foo");
            }
        };

        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
        assertThat(em.reported).isTrue();
    }

    @Test
    public void testRefineMethod_GenericException_throws() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void refine(TestRefinable event, Grammar grammer, Broadcaster b) {
                throw new RuntimeException("foo");
            }
        };

        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        TestRefinable event = new TestRefinable();

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("Exception thrown from a CSS Parser plugin method");
        exception.expectMessage("foo");
        s.refine(event, new Grammar(), new NoopBroadcaster(), em);
    }

    @Test
    public void testProcessMethodWithWrongArgSignatureThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void process() {}
        };
        Method m = subscriber.getClass().getMethod("process");
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.PROCESS, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("does not have expected parameters");
        s.process(event, this.em);
    }

    @Test
    public void testProcessMethodWithPrivateAccessThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            private void process(ClassSelector selector) {}
        };
        Method m = subscriber.getClass().getDeclaredMethod("process", ClassSelector.class);
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.PROCESS, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("method is not accessible");
        s.process(event, this.em);
    }

    @Test
    public void testProcessMethod_GenericException_throws() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void process(ClassSelector selector) {
                throw new RuntimeException("foo");
            }
        };

        Method m = subscriber.getClass().getMethod("process", ClassSelector.class);
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.PROCESS, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("Exception thrown from a CSS Parser plugin method");
        exception.expectMessage("foo");
        s.process(event, this.em);
    }

    @Test
    public void testValidateMethodWithWrongArgSignatureThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void validate(ClassSelector selector) {}
        };
        Method m = subscriber.getClass().getMethod("validate", ClassSelector.class);
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.VALIDATE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("does not have expected parameters");
        s.validate(event, this.em);
    }

    @Test
    public void testValidateMethodWithPrivateAccessThrowsException() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            private void validate(ClassSelector selector, ErrorManager em) {}
        };
        Method m = subscriber.getClass().getDeclaredMethod("validate", ClassSelector.class, ErrorManager.class);
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.VALIDATE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("method is not accessible");
        s.validate(event, this.em);
    }

    @Test
    public void testValidateMethod_GenericException_throws() throws Exception {
        Plugin subscriber = new Plugin() {
            @SuppressWarnings("unused")
            public void validate(ClassSelector selector, ErrorManager em) {
                throw new RuntimeException("foo");
            }
        };

        Method m = subscriber.getClass().getMethod("validate", ClassSelector.class, ErrorManager.class);
        ClassSelector event = new ClassSelector("test");

        Subscription s = new Subscription(SubscriptionPhase.VALIDATE, subscriber, m, null);

        exception.expect(SubscriptionException.class);
        exception.expectMessage("Exception thrown from a CSS Parser plugin method");
        exception.expectMessage("foo");
        s.validate(event, this.em);
    }

    @Test
    public void testEquals_differentInstancesReturnsFalse() throws Exception {
        HasRefineMethod subscriber1 = new HasRefineMethod();
        Method m1 = subscriber1.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);
        HasRefineMethod subscriber2 = new HasRefineMethod();
        Method m2 = subscriber2.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);

        Subscription s1 = new Subscription(SubscriptionPhase.REFINE, subscriber1, m1, null);
        Subscription s2 = new Subscription(SubscriptionPhase.REFINE, subscriber2, m2, null);

        assertThat(s1.equals(s2)).isFalse();
    }

    @Test
    public void testEquals_anotherObjectReturnsFalse() throws Exception {
        HasRefineMethod subscriber1 = new HasRefineMethod();
        Method m1 = subscriber1.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);

        Subscription s1 = new Subscription(SubscriptionPhase.REFINE, subscriber1, m1, null);

        //noinspection ConstantConditions
        assertThat(s1.equals(null)).isFalse();
    }

    @Test
    public void testEquals_sameInstanceAndMethodReturnsTrue() throws Exception {
        HasRefineMethod subscriber1 = new HasRefineMethod();
        Method m1 = subscriber1.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);

        Subscription s1 = new Subscription(SubscriptionPhase.REFINE, subscriber1, m1, null);
        Subscription s2 = new Subscription(SubscriptionPhase.REFINE, subscriber1, m1, null);

        assertThat(s1.equals(s2)).isTrue();
    }

    @Test
    public void testToString() throws Exception {
        HasRefineMethod subscriber = new HasRefineMethod();
        Method m = subscriber.getClass().getMethod(HasRefineMethod.refineMethodName, HasRefineMethod.refineMethodArgs);

        Subscription s = new Subscription(SubscriptionPhase.REFINE, subscriber, m, null);
        assertThat(s.toString()).containsIgnoringCase(HasRefineMethod.refineMethodName);
    }
}