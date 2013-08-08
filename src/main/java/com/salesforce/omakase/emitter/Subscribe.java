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
public @interface Subscribe {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    SubscriptionType type() default SubscriptionType.CREATED;

    /**
     * TODO Description
     * 
     * @return TODO
     */
    String filter() default "";

    /**
     * TODO Description
     * 
     * @return TODO
     */
    int priority() default -1;
}
