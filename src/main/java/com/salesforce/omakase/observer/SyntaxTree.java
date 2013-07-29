/**
 * ADD LICENSE
 */
package com.salesforce.omakase.observer;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.ast.impl.StandardRule;
import com.salesforce.omakase.ast.impl.StandardStylesheet;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SyntaxTree implements Observer {
    private final Stylesheet stylesheet = new StandardStylesheet();
    private final List<String> comments = Lists.newArrayList();

    private Rule rule;

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Stylesheet stylesheet() {
        return stylesheet;
    }

    @Override
    public void comment(String comment) {
        comments.add(comment);
    }

    @Override
    public void selectorGroup(SelectorGroup selectors) {
        // add any associated comments to the selector
        associateComments(selectors);

        // create a new rule with the selector
        rule = new StandardRule(selectors.line(), selectors.column());
        rule.selectorGroup(selectors);

        // add the rule to the stylesheet
        stylesheet.rule(rule);
    }

    @Override
    public void declaration(Declaration declaration) {
        // add any associated comments to the declaration
        associateComments(declaration);

        // add the declaration to the rule
        rule.declaration(declaration);
    }

    private void associateComments(Syntax unit) {
        for (String comment : comments) {
            unit.comment(comment);
        }
        comments.clear();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("stylesheet", stylesheet).toString();
    }
}
