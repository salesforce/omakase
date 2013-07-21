/**
 * ADD LICENSE
 */
package com.salesforce.omakase.observer;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Observer {
    /**
     * TODO Description
     * 
     * @param comment
     *            TODO
     */
    void comment(String comment);

    /**
     * TODO Description
     * 
     * @param selector
     *            TODO
     */
    void selector(Selector selector);

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     */
    void declaration(Declaration declaration);
}
