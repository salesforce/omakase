/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.builder.RuleBuilder;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardRuleBuilder extends AbstractBuilder<Rule> implements RuleBuilder {
    private SelectorGroup selectors;
    private List<Declaration> declarations = Lists.newArrayList();

    /** TODO */
    protected StandardRuleBuilder() {}

    @Override
    public Rule build() {
        return new StandardRule(line, column, selectors, declarations);
    }

    @Override
    public RuleBuilder selectorGroup(SelectorGroup selectorGroup) {
        this.selectors = selectorGroup;
        return this;
    }

    @Override
    public RuleBuilder declaration(Declaration declaration) {
        this.declarations.add(declaration);
        return this;
    }
}
