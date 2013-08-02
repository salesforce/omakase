/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * A {@link Builder} used to create {@link Rule} instances.
 * 
 * @author nmcwilliams
 */
public interface RuleBuilder extends Builder<Rule> {
    /**
     * Specifies the {@link SelectorGroup} containing the {@link Selector}s of the rule.
     * 
     * @param selectorGroup
     *            The {@link SelectorGroup}.
     * @return this, for chaining.
     */
    RuleBuilder selectorGroup(SelectorGroup selectorGroup);

    /**
     * Adds a declaration to the list.
     * 
     * @param declaration
     *            The declaration to add.
     * @return this, for chaining.
     */
    RuleBuilder declaration(Declaration declaration);
}
