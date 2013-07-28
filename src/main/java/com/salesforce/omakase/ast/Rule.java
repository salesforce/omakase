/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Rule extends Syntax {
    /**
     * TODO Description
     * 
     * @param selectors
     *            TODO
     * @return TODO
     */
    Rule selectorGroup(SelectorGroup selectorGroup);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<SelectorGroup> selectorGroups();

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     * @return TODO
     */
    Rule declaration(Declaration declaration);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<Declaration> declarations();
}
