/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.*;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rework {
    /**
     * Subscribe to this type of event (default is {@link SubscriptionType#CREATED}).
     * 
     * @return The {@link SubscriptionType}.
     */
    SubscriptionType type() default SubscriptionType.CREATED;
}
