/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.writer;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A wrapper around an {@link Appendable} that provides a few convenience functions.
 * <p/>
 * When not specifying a particular {@link Appendable} then use {@link #toString()} to get the final output.
 *
 * @author nmcwilliams
 */
public final class StyleAppendable {
    private static final String INDENT_STRING = "            ";
    private final Appendable appendable;
    private int indent = 0;

    /** Creates a new {@link StyleAppendable} using a {@link StringBuilder}. Use {@link #toString()} to get the final output. */
    public StyleAppendable() {
        this(new StringBuilder(256));
    }

    /**
     * Creates a new {@link StyleAppendable} using the given {@link Appendable}.
     *
     * @param appendable
     *     Write to this {@link Appendable}.
     */
    public StyleAppendable(Appendable appendable) {
        this.appendable = checkNotNull(appendable, "appendable cannot be null");
    }

    /**
     * Appends the specified character. Prefer this over {@link #append(CharSequence)}.
     *
     * @param c
     *     The character to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    /**
     * Appends the specified integer.
     *
     * @param i
     *     The integer to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(int i) throws IOException {
        return append(Integer.toString(i));
    }

    /**
     * Appends the specified double.
     *
     * @param d
     *     The double to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(double d) throws IOException {
        return append(Double.toString(d));
    }

    /**
     * Appends the specified long.
     *
     * @param l
     *     The long to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(long l) throws IOException {
        return append(Long.toString(l));
    }

    /**
     * Appends the specified {@link CharSequence} or String.
     *
     * @param sequence
     *     The character sequence to append.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(CharSequence sequence) throws IOException {
        appendable.append(sequence);
        return this;
    }

    /**
     * Appends a newline character.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable newline() throws IOException {
        append('\n');
        if (indent != 0) append(INDENT_STRING.substring(0, indent * 2));
        return this;
    }

    /**
     * Appends a newline character only if the given condition is true.
     *
     * @param condition
     *     Only append a newline if this condition is true.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable newlineIf(boolean condition) throws IOException {
        if (condition) newline();
        return this;
    }

    /**
     * Appends a single space character.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable space() throws IOException {
        return append(' ');
    }

    /**
     * Appends a single space character only if the given condition is true.
     *
     * @param condition
     *     Only append a newline if this condition is true.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable spaceIf(boolean condition) throws IOException {
        if (condition) space();
        return this;
    }

    /**
     * Increases the indentation level. Indentation spaces are appended when {@link #newline()} or {@link #newlineIf(boolean)} are
     * called.
     *
     * @return this, for chaining.
     */
    public StyleAppendable indent() {
        indent++;
        return this;
    }

    /**
     * Increases the indentation level only if the given condition is true. Indentation spaces are appended when {@link
     * #newline()} or {@link #newlineIf(boolean)} are called.
     *
     * @param condition
     *     Only increase the indentation level if this condition is true.
     *
     * @return this, for chaining.
     */
    public StyleAppendable indentIf(boolean condition) {
        if (condition) indent();
        return this;
    }

    /**
     * Decreases the indentation level.
     *
     * @return this, for chaining.
     */
    public StyleAppendable unindent() {
        indent = (indent == 0) ? 0 : indent - 1;
        return this;
    }

    /**
     * Decreases the indentation level only if the given condition is true.
     *
     * @param condition
     *     Only decrease the indentation level if this condition is true.
     *
     * @return this, for chaining.
     */
    public StyleAppendable unindentIf(boolean condition) {
        if (condition) unindent();
        return this;
    }

    /**
     * Gets the current indentation level.
     *
     * @return The current indentation level.
     */
    public int indentationLevel() {
        return indent;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }
}
