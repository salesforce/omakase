/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * Designates that an {@link Syntax} unit is <em>refinable</em> to a more specified or detailed representation.
 * <p/>
 * This is primarily used with high-level {@link Syntax} units. CSS is parsed into unrefined {@link Syntax} units for performance
 * reasons, where each unrefined object can be further refined on demand to obtain and work with the more detailed representation
 * as applicable.
 * <p/>
 * It's important to remember that <em>before being refined</em> refinable objects may actually contain invalid CSS. Simply
 * refining the object will verify it's grammatical compliance, which can be coupled with custom validation to ensure correct
 * usage.
 *
 * @param <T>
 *     Refine to this Type of object.
 *
 * @author nmcwilliams
 * @see Syntax
 */
@Subscribable
@Description("raw syntax that can be further refined")
public interface Refinable<T> extends Syntax {
    /**
     * Refines the object to its more specific and detailed state or representation.
     * <p/>
     * <b>Important</b>: for implementations, this operation must be <em>idempotent</em>.
     *
     * @return The refined object.
     */
    T refine();

    /**
     * Gets whether this unit is refined.
     *
     * @return True if this unit is refined.
     */
    boolean isRefined();
}
