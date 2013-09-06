/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

/**
 * A wrapper around an {@link Appendable} that provides a few convenience functions.
 *
 * When not specifying a particular {@link Appendable} then use {@link #toString()} to get the final output.
 *
 * @author nmcwilliams
 */
public final class StyleAppendable {
    private final Appendable appendable;

    /**
     * Creates a new {@link StyleAppendable} using a {@link StringBuilder}. Use {@link #toString()} to get the final
     * output.
     */
    public StyleAppendable() {
        this(new StringBuilder(256));
    }

    /**
     * Creates a new {@link StyleAppendable} using the given {@link Appendable}.
     *
     * @param appendable
     *            Write to this {@link Appendable}.
     */
    public StyleAppendable(Appendable appendable) {
        this.appendable = checkNotNull(appendable, "appendable canot be null");
    }

    /**
     * Appends the specified character. Prefer this over {@link #append(CharSequence)}.
     *
     * @param c
     *            The character to write.
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    /**
     * Appends the specified {@link CharSequence} or String.
     *
     * @param sequence
     *            The character sequence to append.
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable append(CharSequence sequence) throws IOException {
        appendable.append(sequence);
        return this;
    }

    /**
     * Appends a newline character.
     *
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable newline() throws IOException {
        return append('\n');
    }

    /**
     * Appends a newline character only if the given condition is true.
     *
     * @param condition
     *            Only append a newline if this condition is true.
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable newlineIf(boolean condition) throws IOException {
        if (condition) newline();
        return this;
    }

    /**
     * Appends a single space character.
     *
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable space() throws IOException {
        return append(' ');
    }

    /**
     * Appends a single space character only if the given condition is true.
     *
     * @param condition
     *            Only append a newline if this condition is true.
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable spaceIf(boolean condition) throws IOException {
        if (condition) space();
        return this;
    }

    /**
     * Appends spaces for indentation.
     *
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public StyleAppendable indent() throws IOException {
        return append(' ').append(' ');
    }

    /**
     * Appends spaces for indentation only if the given condition is true.
     *
     * @param condition
     *            Only append a newline if this condition is true.
     * @return this, for chaining.
     * @throws IOException
     *             If an I/O error occurs.
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
