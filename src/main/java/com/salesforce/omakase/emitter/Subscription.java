/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Objects;
import com.salesforce.omakase.As;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.error.OmakaseException;

/**
 * Metadata class to wrap the details around a subscription method. For internal use only.
 * 
 * @author nmcwilliams
 */
final class Subscription {
    private final SubscriptionPhase phase;
    private final SubscriptionType type;
    private final Object subscriber;
    private final Method method;

    Subscription(SubscriptionPhase phase, SubscriptionType type, Object subscriber, Method method) {
        this.phase = phase;
        this.type = type;
        this.subscriber = subscriber;
        this.method = method;
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
     * The type of subscription.
     * 
     * @return The type of subscription.
     */
    public SubscriptionType type() {
        return type;
    }

    /**
     * Inform this subscription that a particular event of a given type occurred. The subscription method may not be
     * invoked, dependent on how restrictive it is with respect to the {@link SubscriptionType}, etc...
     * 
     * @param type
     *            The type of event.
     * @param event
     *            The event object (e.g., syntax instance).
     * @param em
     *            The {@link ErrorManager} instance.
     */
    public void inform(SubscriptionType type, Object event, ErrorManager em) {
        checkNotNull(event, "event cannot be null");

        if (type == SubscriptionType.CREATED) {
            // create events are broadcast to both CREATED and CHANGED
            deliver(event, em);
        } else if (this.type == type) {
            // only broadcast CHANGED events to subscriptions with type CHANGED
            deliver(event, em);
        }
    }

    /**
     * Invokes the subscription method.
     * 
     * @param event
     *            The event object (e.g., syntax instance).
     * @param em
     *            The {@link ErrorManager} instance to use for validation methods.
     */
    private void deliver(Object event, ErrorManager em) {
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
            if (e.getCause() instanceof OmakaseException) throw (OmakaseException)e.getCause();
            throw new SubscriptionException("A problem was encountered while invoking the subscription method", e);
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
        return As.string(this)
            .indent()
            .add("method", method.getName())
            .add("subscriber", subscriber)
            .add("phase", phase)
            .add("type", type)
            .toString();
    }
}
