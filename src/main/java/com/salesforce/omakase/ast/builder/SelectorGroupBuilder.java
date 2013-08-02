/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * A {@link Builder} used to create {@link SelectorGroup} instances.
 * 
 * @author nmcwilliams
 */
public interface SelectorGroupBuilder extends Builder<SelectorGroup> {
    /**
     * Specifies the content of the entire selector group. A selector group contains multiple selectors, with each
     * selector separated by commas.
     * 
     * <p> The content can also contain CSS comments.
     * 
     * @param content
     *            The content of the selector group.
     * @return this, for chaining.
     */
    SelectorGroupBuilder content(String content);
}
