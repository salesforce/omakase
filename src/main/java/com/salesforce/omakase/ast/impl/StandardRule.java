/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class StandardRule extends AbstractSyntax implements Rule {
    private List<SelectorGroup> selectorGroups = Lists.newArrayList();
    private List<Declaration> declarations = Lists.newArrayList();

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public StandardRule(int line, int column) {
        super(line, column);
    }

    @Override
    public Rule selectorGroup(SelectorGroup selectorGroup) {
        selectorGroups.add(selectorGroup);
        return this;
    }

    @Override
    public List<SelectorGroup> selectorGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rule declaration(Declaration declaration) {
        declarations.add(declaration);
        return this;
    }

    @Override
    public List<Declaration> declarations() {
        return declarations;
    }

}
