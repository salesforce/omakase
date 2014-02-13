package com.salesforce.omakase.ast.atrule;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents an expression that should never be written out.
 * <p/>
 * This is usually used by custom metadata at-rules, see {@link AtRule#markAsMetadataRule()}.
 *
 * @author nmcwilliams
 */
public final class MetadataExpression extends AbstractSyntax<AtRuleExpression> implements AtRuleExpression {
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
    protected AtRuleExpression makeCopy(Prefix prefix, SupportMatrix support) {
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
