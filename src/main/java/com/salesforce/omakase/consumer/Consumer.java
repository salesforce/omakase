/**
 * ADD LICENSE
 */
package com.salesforce.omakase.consumer;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Consumer {
    /**
     * TODO Description
     * 
     * @param selectorGroup
     *            TODO
     */
    void selectorGroup(SelectorGroup selectorGroup);

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     */
    void declaration(Declaration declaration);
}
