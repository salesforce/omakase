/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * A refined {@link Declaration}, with the property name and property value fully parsed.
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
