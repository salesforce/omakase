/**
 * ADD LICENSE
 */
package com.salesforce.omakase.consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.builder.*;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.ast.standard.StandardSyntaxFactory;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@NotThreadSafe
public class SyntaxTree implements Consumer {
    private final List<Builder<? extends Statement>> statements;
    private final SyntaxFactory factory;

    private RuleBuilder currentRule;
    private Stylesheet stylesheet;

    /**
     * TODO
     */
    public SyntaxTree() {
        this(StandardSyntaxFactory.instance());
    }

    /**
     * TODO
     * 
     * @param factory
     *            TODO
     */
    public SyntaxTree(SyntaxFactory factory) {
        this.statements = Lists.newArrayList();
        this.factory = checkNotNull(factory, "factory cannot be null");
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Stylesheet stylesheet() {
        if (stylesheet == null) {
            StylesheetBuilder builder = factory.stylesheet();
            for (Builder<? extends Statement> statement : statements) {
                builder.statement(statement.build());
            }
            stylesheet = builder.build();
        }
        return stylesheet;
    }

    @Override
    public void selectorGroup(SelectorGroup selectorGroup) {
        // create a new rule
        currentRule = factory.rule();

        // at the selector group to the rule
        currentRule.selectorGroup(selectorGroup);

        // the rule's position is the same as the selector group
        currentRule.line(selectorGroup.line());
        currentRule.column(selectorGroup.column());

        // add the rule to the list of statements
        statements.add(currentRule);
    }

    @Override
    public void declaration(Declaration declaration) {
        checkState(currentRule != null, "new declaration without a current rule");
        currentRule.declaration(declaration);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("stylesheet", stylesheet())
            .toString();
    }
}
