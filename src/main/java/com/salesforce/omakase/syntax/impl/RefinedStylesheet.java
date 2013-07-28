/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RefinedStylesheet implements Stylesheet {
    List<RefinedRule> rules = Lists.newArrayList();

    /**
     * TODO
     * 
     * @param rawStylesheet
     *            TODO
     */
    public RefinedStylesheet(RawStylesheet rawStylesheet) {
        for (Rule rule : rawStylesheet.rules()) {
            rule(rule);
        }
    }

    @Override
    public RefinedStylesheet refine() {
        return this;
    }

    @Override
    public Stylesheet rule(Rule rule) {
        rules.add(rule.refine());
        return this;
    }

    @Override
    public List<? extends Rule> rules() {
        return rules;
    }
}
