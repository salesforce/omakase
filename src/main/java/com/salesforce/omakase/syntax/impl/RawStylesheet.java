/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

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
public class RawStylesheet implements Stylesheet {
    private final List<Rule> rules = Lists.newArrayList();

    @Override
    public RefinedStylesheet refine() {
        return new RefinedStylesheet(this);
    }

    @Override
    public Stylesheet rule(Rule rule) {
        rules.add(checkNotNull(rule, "rule cannot be null"));
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
