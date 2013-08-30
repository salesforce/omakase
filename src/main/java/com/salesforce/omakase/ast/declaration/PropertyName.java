/**
\ * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Writable;

/**
 * The name of a property in a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
public interface PropertyName extends Writable {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */
    String getName();
}
