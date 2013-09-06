/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import java.io.IOException;

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.declaration.StringValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME A string value, e.g., "Times New Roman".
 * 
 * @see StringValueParser
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "individual string value", broadcasted = REFINED_DECLARATION)
public class StringValue extends AbstractSyntax implements Term {
    private QuotationMode mode;
    private String content;

    /** Type of quotation mark */
    public enum QuotationMode {
        /** single quotes */
        SINGLE,
        /** double quotes */
        DOUBLE
    }

    /**
     * Constructs a new {@link StringValue} instance.
     * 
     * <p>
     * The {@link QuotationMode} is required so that we can preserve the original quotes used in the source.
     * Performance-wise, there is no reason to change it, and also it keeps us from having to mess around with escaping.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     * @param mode
     *            The {@link QuotationMode} to use when printing out the value.
     * @param content
     *            The content of the string.
     */
    public StringValue(int line, int column, QuotationMode mode, String content) {
        super(line, column);
        this.mode = mode;
        this.content = content;
    }

    /**
     * TODO
     * 
     * @param mode
     *            TODO
     * @param content
     *            TODO
     */
    public StringValue(QuotationMode mode, String content) {
        content(mode, content);
    }

    /**
     * Sets the content of the string.
     * 
     * @param mode
     *            The {@link QuotationMode} to use when printing out the value.
     * @param content
     *            The content.
     * @return this, for chaining.
     */
    public StringValue content(QuotationMode mode, String content) {
        this.mode = checkNotNull(mode, "mode cannot be null");
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
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (mode == QuotationMode.SINGLE) {
            appendable.append('\'').append(content).append('\'');
        } else {
            appendable.append('"').append(content).append('"');
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("content", content)
            .toString();
    }
}
