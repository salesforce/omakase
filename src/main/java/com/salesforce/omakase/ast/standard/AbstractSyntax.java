/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import static com.salesforce.omakase.Util.immutable;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;
    private final ImmutableList<String> comments;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param comments
     *            TODO
     */
    public AbstractSyntax(int line, int column, List<String> comments) {
        this.line = line;
        this.column = column;
        this.comments = immutable(comments);
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public List<String> comments() {
        return comments;
    }

    @Override
    public List<String> ownComments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("super", super.toString())
            .add("line", line)
            .add("column", column)
            .add("comments", comments)
            .toString();
    }
}
