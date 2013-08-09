/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Objects;

/**
 * TODO Description
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
     * TODO Description
     * 
     * @return TODO
     */
    public SubscriptionType type() {
        return type;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String filter() {
        return filter;
    }

    /**
     * TODO Description
     * 
     * @param type
     *            TODO
     * @param event
     *            TODO
     */
    public void inform(SubscriptionType type, Object event) {
        checkNotNull(event, "event cannot be null");
        if (this.type == type) {
            deliver(event);
        }
    }

    /**
     * TODO Description
     * 
     * @param event
     *            TOOD
     */
    private void deliver(Object event) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionException("Invalid arguments for subscription method", e);
        } catch (IllegalAccessException e) {
            throw new SubscriptionException("Subscription method is not accessible", e);
        } catch (InvocationTargetException e) {
            throw new SubscriptionException("Could not invoke the subscription method", e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subscriber, method);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Subscription) {
            Subscription that = (Subscription)object;
            return subscriber == that.subscriber && Objects.equal(this.method, that.method);
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
