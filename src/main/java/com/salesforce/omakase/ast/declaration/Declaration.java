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
     * TODO Description
     * 
     * @return TODO
     */
    String content();
}
