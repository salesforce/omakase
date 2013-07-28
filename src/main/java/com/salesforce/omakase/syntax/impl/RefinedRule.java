/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import java.util.List;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RefinedRule extends AbstractSyntaxUnit implements Rule {

    /**
     * @param line
     * @param column
     */
    public RefinedRule(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param rawRule
     */
    public RefinedRule(RawRule rawRule) {
        super(rawRule.line(), rawRule.column());
    }

    @Override
    public RefinedRule refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rule selector(Selector selector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Selector selector() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rule declaration(Declaration declaration) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Declaration> declarations() {
        // TODO Auto-generated method stub
        return null;
    }

}
