/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description of a {@link Subscribable} type.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Description {
    /**
     * A description of the item receiving this annotation.
     *
     * @return The description.
     */
    String value() default "(no description)";

    /**
     * Indicates the conditions for this type of object to be broadcasted.
     *
     * @return The conditions for this type of object to be broadcasted.
     */
    SubscribableRequirement broadcasted() default SubscribableRequirement.AUTOMATIC;
}
