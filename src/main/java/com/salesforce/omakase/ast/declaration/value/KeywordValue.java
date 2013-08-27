/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.EmittableRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * A keyword value (e.g., inline-block).
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "individual keyword value", broadcasted = REFINED_DECLARATION)
public class KeywordValue extends AbstractSyntax implements Term {
    private String keyword;

    /**
     * Constructs a new {@link KeywordValue} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param keyword
     *            The keyword.
     */
    public KeywordValue(int line, int column, String keyword) {
        super(line, column);
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
