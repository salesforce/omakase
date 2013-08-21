/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * A string value.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "individual string value", broadcasted = REFINED_DECLARATION)
public class StringValue extends AbstractSyntax implements Term {
    private String content;

    /**
     * Constructs a new {@link StringValue} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param content
     *            The content of the string.
     */
    public StringValue(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }

    /**
     * Sets the content of the string.
     * 
     * @param content
     *            The content.
     * @return this, for chaining.
     */
    public StringValue content(String content) {
        this.content = checkNotNull(content, "content cannot be null");
        return this;
    }

    /**
     * Gets the content of the string.
     * 
     * @return The content of the string.
     */
    public String content() {
        return content;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("content", content)
            .toString();
    }
}
