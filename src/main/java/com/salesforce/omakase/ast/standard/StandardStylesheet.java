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
     * @param comments
     *            TODO
     * @param statements
     *            TODO
     */
    public StandardStylesheet(int line, int column, List<String> comments, List<Statement> statements) {
        super(line, column, comments);
        this.statements = immutable(statements);
    }

    @Override
    public List<Statement> statements() {
        return statements;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("super", super.toString())
            .add("statements", statements)
            .toString();
    }
}
