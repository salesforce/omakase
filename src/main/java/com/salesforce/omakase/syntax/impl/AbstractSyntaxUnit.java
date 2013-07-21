/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.syntax.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class AbstractSyntaxUnit implements Syntax {
    private final int line;
    private final int column;

    private List<String> comments;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public AbstractSyntaxUnit(int line, int column) {
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
}
