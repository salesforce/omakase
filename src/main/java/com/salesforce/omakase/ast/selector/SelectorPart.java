/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.Linkable;
import com.salesforce.omakase.ast.Syntax;

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
