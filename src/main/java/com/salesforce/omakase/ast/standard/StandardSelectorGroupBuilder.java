/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.builder.SelectorGroupBuilder;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSelectorGroupBuilder extends AbstractBuilder<SelectorGroup> implements SelectorGroupBuilder {
    /** TODO */
    protected String content;

    /** TODO */
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
