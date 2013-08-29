/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class StyleAppendable {
    private final Appendable appendable;

    /**
     * TODO
     */
    public StyleAppendable() {
        this(new StringBuilder(256));
    }

    /**
     * TODO
     * 
     * @param appendable
     *            TODO
     */
    public StyleAppendable(Appendable appendable) {
        this.appendable = checkNotNull(appendable, "appendable canot be null");
    }

    /**
     * TODO Description
     * 
     * @param c
     *            TODO
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    /**
     * TODO Description
     * 
     * @param sequence
     *            TODO
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable append(CharSequence sequence) throws IOException {
        appendable.append(sequence);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable newline() throws IOException {
        return append('\n');
    }

    /**
     * TODO Description
     * 
     * @param condition
     *            TODO
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable newlineIf(boolean condition) throws IOException {
        if (condition) newline();
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable space() throws IOException {
        return append(' ');
    }

    /**
     * TODO Description
     * 
     * @param condition
     *            TODO
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable spaceIf(boolean condition) throws IOException {
        if (condition) space();
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable indent() throws IOException {
        return append(' ').append(' ');
    }

    /**
     * TODO Description
     * 
     * @param condition
     *            TODO
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public StyleAppendable indentIf(boolean condition) throws IOException {
        if (condition) indent();
        return this;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }
}
