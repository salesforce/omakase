/**
 * ADD LICENSE
 */
package com.salesforce.omakase.observer;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Observer {
    /**
     * TODO Description
     * 
     * @param comment
     *            TODO
     */
    void comment(String comment);

    /**
     * TODO Description
     * 
     * @param selectors
     *            TODO
     */
    void selectorGroup(SelectorGroup selectors);

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     */
    void declaration(Declaration declaration);
}
