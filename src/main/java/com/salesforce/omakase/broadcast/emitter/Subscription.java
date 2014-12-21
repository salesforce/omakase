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
