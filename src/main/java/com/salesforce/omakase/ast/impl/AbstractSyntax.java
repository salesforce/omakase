/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;

    private List<String> comments;
    private boolean dirty;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public AbstractSyntax(int line, int column) {
        this.line = line;
        this.column = column;
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
    public void comment(String comment) {
        if (comments == null) {
            comments = Lists.newArrayList();
        }
        comments.add(checkNotNull(comment, "comment cannot be null"));
    }

    @Override
    public List<String> comments() {
        if (comments == null) {
            comments = Lists.newArrayList();
        }
        return comments;
    }

    @Override
    public List<String> ownComments() {
        // TODO Auto-generated method stub
        return null;
    }

    protected boolean dirty() {
        return dirty;
    }

    protected void dirty(boolean dirty) {
        this.dirty = dirty;
    }
}
