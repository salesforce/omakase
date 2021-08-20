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

package com.salesforce.omakase.broadcast.emitter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.As;

/**
 * Metadata class to wrap the details around a subscription method. For internal use only.
 *
 * @author nmcwilliams
 */
final class Subscription implements Comparable<Subscription> {
    private static final AtomicInteger counter = new AtomicInteger();

    private final SubscriptionPhase phase;
    private final Object subscriber;
    private final Method method;
    private final String name;
    private final int number;

    Subscription(SubscriptionPhase phase, Object subscriber, Method method, String name) {
        this.phase = phase;
        this.subscriber = subscriber;
        this.method = method;
        this.name = name != null ? name.toLowerCase() : null;
        this.number = counter.addAndGet(1);
    }

    public SubscriptionPhase phase() {
        return phase;
    }

    public Method method() {
        return method;
    }

    /** deliver a refine subscription */
    public void refine(Broadcastable event, Grammar grammar, Broadcaster broadcaster, ErrorManager em) {
        if (name != null && !filter(event)) return;

        try {
            method.invoke(subscriber, event, grammar, broadcaster);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("CSS Parser plugin 'refine' method does not have expected parameters (3)", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("CSS Parser plugin 'refine' method is not accessible", e);
        } catch (InvocationTargetException e) {
            handlePluginError(e, em, "Exception thrown from a CSS Parser plugin method during 'refine'");
        }
    }

    /** deliver a rework/observe subscription */
    public void process(Broadcastable event, ErrorManager em) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("CSS Parser plugin method does not have expected parameters (1)", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("CSS Parser plugin method is not accessible", e);
        } catch (InvocationTargetException e) {
            handlePluginError(e, em, "Exception thrown from a CSS Parser plugin method");
        }
    }

    /** deliver a validate subscription */
    public void validate(Broadcastable event, ErrorManager em) {
        try {
            method.invoke(subscriber, event, em);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("CSS Parser plugin 'validate' method does not have expected parameters (2)", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("CSS Parser plugin 'validate' method is not accessible", e);
        } catch (InvocationTargetException e) {
            handlePluginError(e, em, "Exception thrown from a CSS Parser plugin method during 'validate'");
        }
    }

    /**
     * Checks whether the subscription should be delivered based on subscription method restrictions.
     * <p>
     * 1. If this subscription is restricted to a certain name (e.g., name of a raw function) then this will return true if the
     * event is an instance of {@link Named} and the name matches (case insensitive), otherwise it will return false.
     * <p>
     * ...Otherwise returns true.
     */
    private boolean filter(Broadcastable event) {
        return name == null || (event instanceof Named && ((Named)event).name().toLowerCase().equals(name));
    }

    private void handlePluginError(Throwable t, ErrorManager em, String msg) {
        if (t.getCause() instanceof ParserException) {
            em.report((ParserException)t.getCause());
        } else if (t.getCause() instanceof SubscriptionException) {
            em.report((SubscriptionException)t.getCause());
        } else {
            throw new SubscriptionException(msg, t);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, method);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Subscription) {
            Subscription other = (Subscription)object;
            // must be same instance of the same class (identity)
            // this keeps plugins that are registered twice only stored once in the Emitter
            return subscriber == other.subscriber && Objects.equals(this.method, other.method);
        }
        return false;
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }

    @Override
    public int compareTo(Subscription o) {
        return Integer.compare(number, o.number);
    }
}
