/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.Builder;
import com.salesforce.omakase.ast.builder.SelectorBuilder;
import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Builder} used to create {@link Selector} instances.
 * 
 * @author nmcwilliams
 */
public class StandardSelectorBuilder extends AbstractBuilder<Selector> implements SelectorBuilder {
    /** the selector content. */
    protected String content;

    /** use a {@link SyntaxFactory} instead. */
    protected StandardSelectorBuilder() {}

    @Override
    public Selector build() {
        return new StandardSelector(line, column, content);
    }

    @Override
    public SelectorBuilder content(String content) {
        this.content = content;
        return this;
    }
}
