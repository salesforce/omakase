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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.annotation.Restrict;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.util.As;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Metadata class to wrap the details around a subscription method. For internal use only.
 *
 * @author nmcwilliams
 */
final class Subscription implements Comparable<Subscription> {
    @SuppressWarnings("StaticNonFinalField") private static int counter;

    private final SubscriptionPhase phase;
    private final Object subscriber;
    private final Method method;
    private final Restrict filter;
    private final int number;

    Subscription(SubscriptionPhase phase, Object subscriber, Method method, Restrict filter) {
        this.phase = phase;
        this.subscriber = subscriber;
        this.method = method;
        this.filter = filter;
        this.number = ++counter;
    }

    /**
     * Gets the {@link SubscriptionPhase} this subscription takes part in.
     *
     * @return The {@link SubscriptionPhase}.
     */
    public SubscriptionPhase phase() {
        return phase;
    }

    /**
     * Gets the method for the subscription.
     *
     * @return The method.
     */
    public Method method() {
        return method;
    }

    /**
     * Gets the {@link Restrict} annotation for the subscription method, or {@link Optional#absent()} if not specified.
     *
     * @return The {@link Restrict} annotation for the subscription method.
     */
    public Optional<Restrict> restriction() {
        return Optional.fromNullable(filter);
    }

    /**
     * Invokes the subscription method.
     *
     * @param event
     *     The event object (e.g., syntax instance).
     * @param em
     *     The {@link ErrorManager} instance to use for validation methods.
     */
    public void deliver(Broadcastable event, ErrorManager em) {
        if (filter != null && !filter(event)) return;

        try {
            if (phase == SubscriptionPhase.VALIDATE) {
                method.invoke(subscriber, event, em);
            } else {
                method.invoke(subscriber, event);
            }
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("Invalid arguments for subscription method", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("Subscription method is not accessible", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) throw (RuntimeException)e.getCause();
            throw new SubscriptionException("A problem was encountered while invoking the subscription method", e);
        }
    }

    /**
     * Filter out units as requested by the optional {@link Restrict} annotation on the subscription method.
     *
     * @param event
     *     The event object (e.g., syntax instance).
     *
     * @return True if the unit should be delivered, false if it should be skipped.
     */
    private boolean filter(Broadcastable event) {
        if (filter != null) {
            if (event instanceof Refinable) {
                // filter out units without raw syntax if requested (e.g., dynamically created units)
                if (!filter.dynamicUnits() && !((Refinable<?>)event).containsRawSyntax()) {
                    return false;
                }
                // filter out units with raw syntax if requested (e.g., parser created units)
                if (!filter.rawUnits() && ((Refinable<?>)event).containsRawSyntax()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subscriber, method);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Subscription) {
            Subscription other = (Subscription)object;
            // must be same instance of the same class (identity)
            // this keeps plugins that are registered twice only stored once in the Emitter
            return subscriber == other.subscriber && Objects.equal(this.method, other.method);
        }
        return false;
    }

    @Override
    public String toString() {
        return As.string(this).fields().toString();
    }

    @Override
    public int compareTo(Subscription o) {
        return (number < o.number) ? -1 : ((number == o.number) ? 0 : 1);
    }
}
