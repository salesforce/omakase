/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import com.salesforce.omakase.syntax.impl.RefinedSelector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Selector extends Syntax, Refinable<RefinedSelector> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    String selector();
}
