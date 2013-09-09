/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

/**
 * TESTME
 * <p/>
 * A keyword value (e.g., inline-block).
 *
 * @author nmcwilliams
 * @see KeywordValueParser
 */
@Subscribable
@Description(value = "individual keyword value", broadcasted = REFINED_DECLARATION)
public class KeywordValue extends AbstractSyntax implements Term {
    private String keyword;

    /**
     * Constructs a new {@link KeywordValue} instance.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(int line, int column, String keyword) {
        super(line, column);
        this.keyword = keyword;
    }

    /**
     * Constructs a new {@link KeywordValue} instance (used for dynamically created {@link Syntax} units). Prefer {@link
     * #KeywordValue(Keyword)} over this.
     *
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(String keyword) {
        keyword(keyword);
    }

    /**
     * Constructs a new {@link KeywordValue} instance with the given {@link Keyword} (used for dynamically created {@link Syntax}
     * units).
     *
     * @param keyword
     *     The keyword.
     */
    public KeywordValue(Keyword keyword) {
        keyword(keyword);
    }

    /**
     * Sets the keyword value. Prefer {@link #keyword(Keyword)} over this one.
     *
     * @param keyword
     *     The keyword.
     *
     * @return this, for chaining.
     */
    public KeywordValue keyword(String keyword) {
        this.keyword = checkNotNull(keyword, "keyword cannot be null");
        return this;
    }

    /**
     * Sets the keyword value.
     *
     * @param keyword
     *     The keyword.
     *
     * @return this, for chaining.
     */
    public KeywordValue keyword(Keyword keyword) {
        return keyword(keyword.toString());
    }

    /**
     * Gets the keyword value.
     *
     * @return The keyword.
     */
    public String keyword() {
        return keyword;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(keyword);
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("keyword", keyword)
            .toString();
    }

    /**
     * Creates a new {@link KeywordValue} instance from the given keyword string. Prefer to use {@link #of(Keyword)} over this.
     * <p/>
     * Example:
     * <pre>
     * <code>KeywordValue.of("left");</code>
     * </pre>
     *
     * @param keyword
     *     The keyword.
     *
     * @return The new {@link KeywordValue} instance.
     */
    public static KeywordValue of(String keyword) {
        return new KeywordValue(keyword);
    }

    /**
     * Creates a new {@link KeywordValue} instance from the given {@link Keyword}.
     * <p/>
     * Example:
     * <pre>
     * <code>KeywordValue.of(Keyword.LEFTF);</code>
     * </pre>
     *
     * @param keyword
     *     The keyword.
     *
     * @return The new {@link KeywordValue} instance.
     */
    public static KeywordValue of(Keyword keyword) {
        return new KeywordValue(keyword);
    }
}
