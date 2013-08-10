/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
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
