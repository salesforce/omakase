package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents an expression that should never be written out.
 * <p/>
 * This is usually used by custom metadata at-rules, see {@link AtRule#markAsMetadataRule()}.
 *
 * @author nmcwilliams
 */
public final class MetadataExpression extends AbstractAtRuleExpression {
    private static final MetadataExpression INSTANCE = new MetadataExpression();

    /**
     * Gets the instance.
     *
     * @return The instance.
     */
    public static MetadataExpression instance() {
        return INSTANCE;
    }

    @Override
    public MetadataExpression copy() {
        return INSTANCE;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) {
    }
}
