/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.SelectorBuilder;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSelectorBuilder extends AbstractBuilder<Selector> implements SelectorBuilder {
    /** TODO */
    protected String content;

    /** TODO */
    protected StandardSelectorBuilder() {}

    @Override
    public Selector build() {
        return new StandardSelector(line, column, comments, content);
    }

    @Override
    public SelectorBuilder content(String content) {
        this.content = content;
        return this;
    }
}
