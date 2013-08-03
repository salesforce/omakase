/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedDeclaration extends Declaration {
    /**
     * Gets the property name.
     * 
     * @return The property name.
     */

    String property();

    /**
     * Gets the property value.
     * 
     * @return The property value.
     */

    String value();
}
