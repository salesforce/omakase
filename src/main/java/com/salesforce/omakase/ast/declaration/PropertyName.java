/**
\ * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.emitter.Subscribable;

/**
 * The name of a property in a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
@Subscribable
public interface PropertyName {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */
    String getName();
}
