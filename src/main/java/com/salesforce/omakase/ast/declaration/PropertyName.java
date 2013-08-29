/**
\ * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Writeable;

/**
 * The name of a property in a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
public interface PropertyName extends Writeable {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */
    String getName();
}
