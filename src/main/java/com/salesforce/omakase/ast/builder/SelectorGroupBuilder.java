/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorGroupBuilder extends Builder<SelectorGroup> {
    /**
     * TODO Description
     * 
     * @param content
     *            TODO
     * @return TODO
     */
    SelectorGroupBuilder content(String content);
}
