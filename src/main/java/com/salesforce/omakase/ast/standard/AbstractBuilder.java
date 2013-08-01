/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.builder.Builder;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
abstract class AbstractBuilder<T extends Syntax> implements Builder<T> {
    /** TODO */
    protected int line = -1;
    /** TODO */
    protected int column = -1;
    /** TODO */
    protected List<String> comments;

    @Override
    public Builder<T> line(int line) {
        this.line = line;
        return this;
    }

    @Override
    public Builder<T> column(int column) {
        this.column = column;
        return this;
    }

    @Override
    public Builder<T> comment(String comment) {
        if (comments == null) {
            comments = Lists.newArrayList();
        }
        comments.add(comment);
        return this;
    }
}
