/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * A keyword value (e.g., inline-block).
 * 
 * @author nmcwilliams
 */
public class KeywordValue implements Term {
    private String keyword;

    /**
     * Constructs a new {@link KeywordValue} instance.
     * 
     * @param keyword
     *            The keyword.
     */
    public KeywordValue(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Sets the keyword value.
     * 
     * @param keyword
     *            The keyword.
     * @return this, for chaining.
     */
    public KeywordValue keyword(String keyword) {
        this.keyword = checkNotNull(keyword, "keyword cannot be null");
        return this;
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
    public String toString() {
        return As.string(this)
            .add("keyword", keyword)
            .toString();
    }
}
