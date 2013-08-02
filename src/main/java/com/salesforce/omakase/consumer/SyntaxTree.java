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
    private final SyntaxFactory factory;
    private final List<Builder<? extends Statement>> statements;

    private RuleBuilder currentRuleBuilder;
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
        this.factory = checkNotNull(factory, "factory cannot be null");
        this.statements = Lists.newArrayList();
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
        currentRuleBuilder = factory.rule();
        currentRuleBuilder.selectorGroup(selectorGroup);
        currentRuleBuilder.line(selectorGroup.line());
        currentRuleBuilder.column(selectorGroup.column());
        statements.add(currentRuleBuilder);
    }

    @Override
    public void declaration(Declaration declaration) {
        checkState(currentRuleBuilder != null, "cannot handle a declaration without a current rule");
        currentRuleBuilder.declaration(declaration);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("stylesheet", stylesheet())
            .toString();
    }

}
