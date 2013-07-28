/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

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
