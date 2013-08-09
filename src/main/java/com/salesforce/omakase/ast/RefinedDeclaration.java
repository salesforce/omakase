/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedDeclaration {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */

    Property property();

    /**
     * Gets the property value.
     * 
     * @return The property value.
     */

    String value();
}
