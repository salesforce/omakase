/**
 * ADD LICENSE
 */
package com.salesforce.omakase.adapter;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.Rule;
import com.salesforce.omakase.syntax.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Adapter {
    /**
     * TODO Description
     */
    void beginRule();

    /**
     * TODO Description
     * 
     */
    void endRule();

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
