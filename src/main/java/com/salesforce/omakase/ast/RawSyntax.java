/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RawSyntax extends AbstractSyntax {
    private final String content;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param rawcontentContent
     *            TODO
     */
    public RawSyntax(int line, int column, String rawcontentContent) {
        super(line, column);
        this.content = rawcontentContent;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String content() {
        return content;
    }

    @Override
    public String filterName() {
        return "";
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("content", content)
            .add("comments", comments())
            .toString();
    }
}
