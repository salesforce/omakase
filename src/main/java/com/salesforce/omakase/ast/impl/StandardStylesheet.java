/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardStylesheet implements Stylesheet {
    private final List<Rule> rules = Lists.newArrayList();

    @Override
    public Stylesheet rule(Rule rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public List<Rule> rules() {
        return rules;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("rules", rules).toString();
    }
}
