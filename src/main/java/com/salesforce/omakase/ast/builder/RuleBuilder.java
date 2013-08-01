/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RuleBuilder extends Builder<Rule> {
    /**
     * TODO Description
     * 
     * @param selectorGroup
     *            TODO
     * @return TODO
     */
    RuleBuilder selectorGroup(SelectorGroup selectorGroup);

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     * @return TODO
     */
    RuleBuilder declaration(Declaration declaration);
}
