/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.*;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    /**
     * Subscribe to this type of event (default is {@link SubscriptionType#CREATED}).
     * 
     * @return The {@link SubscriptionType}.
     */
    SubscriptionType type() default SubscriptionType.CREATED;

    /**
     * Only receive events for Syntax units with a {@link Syntax#filterName()} matching this value. An example would be
     * restricting {@link Declaration}s to a particular property name.
     * 
     * @return The filter
     */
    String filter() default "";

    /**
     * The order in which the method should be registered, with respect to <em>other methods within the same class</em>.
     * Note that this does not have an effect on methods that subscribe to {@link Syntax} units within the same
     * hierarchy. Within the same hierarchy, the more specifically typed subscription will be invoked before more
     * generally
     * typed one.
     * 
     * <p>
     * Usage of this parameter is intended to ensure that validation subscriptions always run before or after rework
     * subscriptions within the same class, as appropriate. Or to ensure that rework subscriptions of the exact same
     * type within a class run in a specific order.
     * 
     * @return The ordering priority.
     */
    int priority() default -1;
}
