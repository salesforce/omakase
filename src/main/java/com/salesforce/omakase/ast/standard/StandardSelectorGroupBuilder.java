/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.Builder;
import com.salesforce.omakase.ast.builder.SelectorGroupBuilder;
import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * A {@link Builder} used to create {@link SelectorGroup} instances.
 * 
 * @author nmcwilliams
 */
public class StandardSelectorGroupBuilder extends AbstractBuilder<SelectorGroup> implements SelectorGroupBuilder {
    /** the selector group content. */
    protected String content;

    /** use a {@link SyntaxFactory} instead. */
    protected StandardSelectorGroupBuilder() {}

    @Override
    public SelectorGroup build() {
        return new StandardSelectorGroup(line, column, content);
    }

    @Override
    public SelectorGroupBuilder content(String content) {
        this.content = content;
        return this;
    }
}
