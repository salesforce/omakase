/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import java.util.List;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.Rule;
import com.salesforce.omakase.syntax.Selector;

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
