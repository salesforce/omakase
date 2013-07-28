/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.syntax.impl.RefinedDeclaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Declaration extends Syntax, Refinable<RefinedDeclaration> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    String property();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    String value();
}
