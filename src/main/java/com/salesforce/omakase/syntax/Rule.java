/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import java.util.List;

import com.salesforce.omakase.syntax.impl.RefinedRule;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Rule extends Syntax, Refinable<RefinedRule> {
    Rule selector(Selector selector);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    Selector selector();

    Rule declaration(Declaration declaration);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<Declaration> declarations();
}
