/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * Designates that an object is <em>refinable</em> to a more specified or detailed representation.
 * 
 * <p>
 * This is primarily used with high-level {@link Syntax} objects. CSS is parsed into unrefined {@link Syntax} objects
 * for performance reasons, where each unrefined object can be further refined on demand to obtain and work with the
 * more detailed representation as applicable.
 * 
 * @see Syntax
 * @param <T>
 *            Refine to this Type of object.
 * 
 * @author nmcwilliams
 */
@Emittable
@Description("raw syntax that can be further refined")
public interface Refinable<T> extends Syntax {
    /**
     * Refines the object to its more specific and detailed state or representation. This may return the same object
     * with its internal state altered, or it may return a new object altogether.
     * 
     * <p>
     * For implementations, this operation must be <em>idempotent</em>.
     * 
     * @return The refined object.
     */
    T refine();
}
