/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Objects;

/**
 * Metadata class to wrap the details around a subscription method. For internal use only.
 * 
 * @author nmcwilliams
 */
public final class Subscription {
    private final SubscriptionType type;
    private final Object subscriber;
    private final Method method;
    private final String filter;

    Subscription(SubscriptionType type, Object subscriber, Method method, String filter) {
        this.type = type;
        this.subscriber = subscriber;
        this.method = method;
        this.filter = filter;
    }

    /**
     * The type of subscription.
     * 
     * @return The type of subscription.
     */
    public SubscriptionType type() {
        return type;
    }

    /**
     * The filter name.
     * 
     * @return the filter name.
     */
    public String filter() {
        return filter;
    }

    /**
     * Inform this subscription that a particular event of a given type occurred. The subscription method may not be
     * invoked, dependent on how restrictive it is with respect to the {@link SubscriptionType}, etc...
     * 
     * @param type
     *            The type of event.
     * @param event
     *            The event object (e.g., syntax instance).
     */
    public void inform(SubscriptionType type, Object event) {
        checkNotNull(event, "event cannot be null");
        if (this.type == type) {
            deliver(event);
        }
    }

    /**
     * Invokes the subscription method.
     * 
     * @param event
     *            The event object (e.g., syntax instance).
     */
    private void deliver(Object event) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("Invalid arguments for subscription method", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("Subscription method is not accessible", e);
        } catch (InvocationTargetException e) {
            throw new SubscriptionException("There was a problem invoking the subscription method", e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subscriber, method);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Subscription) {
            Subscription other = (Subscription)object;
            return subscriber == other.subscriber && Objects.equal(this.method, other.method);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("method", method.getName())
            .add("subscriber", subscriber)
            .add("type", type)
            .add("filter", filter)
            .toString();
    }
}
