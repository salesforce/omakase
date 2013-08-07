/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

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
     * @param part
     *            TODO
     * @return TODO
     */
    RefinedSelector part(SelectorPart part);

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
