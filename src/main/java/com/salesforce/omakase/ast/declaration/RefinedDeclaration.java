/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Syntax;

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
