/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * Standard implementation of a {@link Stylesheet}
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardStylesheet extends AbstractSyntax implements Stylesheet {
    private final List<Statement> statements = Lists.newArrayList();

    StandardStylesheet(int line, int column, Iterable<Statement> statements) {
        super(line, column);
        Iterables.addAll(this.statements, statements);
    }

    @Override
    public Stylesheet statement(Statement statement) {
        statements.add(statement);
        return this;
    }

    @Override
    public List<Statement> statements() {
        return statements;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("statements", Joiner.on("\n\n").join(statements))
            .toString();
    }
}
