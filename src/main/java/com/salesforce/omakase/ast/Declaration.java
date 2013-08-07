/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
public interface Declaration extends Syntax, Linkable<Declaration>, Refinable<RefinedDeclaration> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    String original();
}
