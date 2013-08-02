/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
public interface Declaration extends Syntax, Refinable<RefinedDeclaration> {
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
