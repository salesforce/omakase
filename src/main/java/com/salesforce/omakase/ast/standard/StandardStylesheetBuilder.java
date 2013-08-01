/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.builder.StylesheetBuilder;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardStylesheetBuilder extends AbstractBuilder<Stylesheet> implements StylesheetBuilder {
    /** TODO */
    protected List<Statement> statements = Lists.newArrayList();

    /** TODO */
    protected StandardStylesheetBuilder() {}

    @Override
    public Stylesheet build() {
        line = Math.max(line, 0);
        column = Math.max(line, 0);
        return new StandardStylesheet(line, column, statements);
    }

    @Override
    public StylesheetBuilder statement(Statement statement) {
        statements.add(statement);
        return this;
    }

    @Override
    public StylesheetBuilder rule(Rule rule) {
        return statement(rule);
    }
}
