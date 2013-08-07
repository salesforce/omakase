/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedDeclaration extends Declaration {
    /**
     * TODO Description
     * 
     * @param property
     *            TODO
     * @return TODO
     */
    RefinedDeclaration property(Property property);

    /**
     * Gets the property name.
     * 
     * @return The property name.
     */

    Property property();

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    RefinedDeclaration value(String value);

    /**
     * Gets the property value.
     * 
     * @return The property value.
     */

    String value();
}
