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
    private final Object target;
    private final Method method;

    Subscription(Object target, Method method) {
        this.target = checkNotNull(target, "target cannot be null");
        this.method = checkNotNull(method, "method cannot be null");
    }

    /**
     * TODO Description
     * 
     * @param event
     *            TOOD
     */
    public void deliver(Object event) {
        checkNotNull(event, "event cannot be null");
        try {
            method.invoke(target, new Object[] { event });
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
        return Objects.hashCode(target, method);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Subscription) {
            Subscription that = (Subscription)object;
            return Objects.equal(this.target, that.target) && Objects.equal(this.method, that.method);
        }
        return false;
    }
}
