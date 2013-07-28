/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.syntax.impl.RefinedRule;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Rule extends Syntax, Refinable<RefinedRule> {
    /**
     * TODO Description
     * 
     * @param selectors
     *            TODO
     * @return TODO
     */
    Rule selectors(SelectorGroup selectors);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorGroup selectors();

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
