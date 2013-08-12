/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.As;

/**
 * Represents raw, non-validated content. Usually used by {@link Refinable}s.
 * 
 * @author nmcwilliams
 */
public class RawSyntax extends AbstractSyntax {
    private final String content;

    /**
     * Creates an instance with the given line and column number and content.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param content
     *            The raw content.
     */
    public RawSyntax(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }

    /**
     * Gets the raw content.
     * 
     * @return The raw content.
     */
    public String content() {
        return content;
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
