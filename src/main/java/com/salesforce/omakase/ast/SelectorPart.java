/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorPart extends Syntax, Linkable<SelectorPart> {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isSelector();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isCombinator();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorPartType type();
}
