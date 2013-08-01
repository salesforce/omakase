/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorBuilder extends Builder<Selector> {
    /**
     * TODO Description
     * 
     * @param content
     *            TODO
     * @return TODO
     */
    SelectorBuilder content(String content);
}
