/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.builder.Builder;
import com.salesforce.omakase.ast.builder.StylesheetBuilder;
import com.salesforce.omakase.ast.builder.SyntaxFactory;

/**
 * A {@link Builder} used to create {@link Stylesheet} instances.
 * 
 * @author nmcwilliams
 */
public class StandardStylesheetBuilder extends AbstractBuilder<Stylesheet> implements StylesheetBuilder {
    /** the statements (e.g., rules and at rules) */
    protected List<Statement> statements = Lists.newArrayList();

    /** use a {@link SyntaxFactory} instead. */
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
}
