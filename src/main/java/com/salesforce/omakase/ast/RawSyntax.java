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
    private final String rawContent;

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param rawContent
     *            TODO
     */
    protected RawSyntax(int line, int column, String rawContent) {
        super(line, column);
        this.rawContent = rawContent;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String rawContent() {
        return rawContent;
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
            .add("rawContent", rawContent)
            .toString();
    }
}
