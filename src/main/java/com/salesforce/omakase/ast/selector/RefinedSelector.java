/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import java.util.List;


/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedSelector extends Selector {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<SelectorPart> parts();

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param type
     *            TODO
     * @return TODO
     */
    <T extends SelectorPart> List<T> parts(Class<T> type);
}
