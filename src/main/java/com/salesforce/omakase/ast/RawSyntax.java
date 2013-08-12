/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;

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
        return As.string(this)
            .add("line", line())
            .add("column", column())
            .add("comments", comments())
            .add("content", content)
            .toString();
    }
}
