/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector.part;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorPart extends Syntax {

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isFirst();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    boolean isLast();

    /**
     * If this selector part is the key selector. FSame as {@link #isLast()}.
     * 
     * @return TODO
     */
    boolean isKey();

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
