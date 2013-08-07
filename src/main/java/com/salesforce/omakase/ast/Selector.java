/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Selector extends Syntax, Linkable<Selector>, Refinable<RefinedSelector> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    String original();
}
