/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import static com.salesforce.omakase.Util.immutable;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class StandardStylesheet extends AbstractSyntax implements Stylesheet {
    private final ImmutableList<Statement> statements;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param statements
     *            TODO
     */
    public StandardStylesheet(int line, int column, List<Statement> statements) {
        super(line, column);
        this.statements = immutable(statements);
    }

    @Override
    public List<Statement> statements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(256);

        for (Statement statement : statements) {
            builder.append("\n\n").append(statement);
        }

        return Objects.toStringHelper(this)
            .add("statements", builder.toString())
            .toString();
    }
}
