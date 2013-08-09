/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedDeclaration extends Syntax {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */

    PropertyName propertyName();

    /**
     * Gets the property value.
     * 
     * @return The property value.
     */

    PropertyValue propertyValue();
}
