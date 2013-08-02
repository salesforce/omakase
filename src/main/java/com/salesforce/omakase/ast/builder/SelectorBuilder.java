/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Builder} used to create {@link Selector} instances.
 * 
 * <p> Note that {@link SelectorGroupBuilder} should be used instead for the entire selector string.
 * 
 * @author nmcwilliams
 */
public interface SelectorBuilder extends Builder<Selector> {
    /**
     * Specifies the content of the selector. For example, "#id > .class". This is for the individual selectors, i.e.,
     * don't include commas. If the string has commas then use {@link SelectorGroupBuilder} instead.
     * 
     * <p> The content can also contain CSS comments.
     * 
     * @param content
     *            The content of the selector.
     * @return this, for chaining.
     */
    SelectorBuilder content(String content);
}
