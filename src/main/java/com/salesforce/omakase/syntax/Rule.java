/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import java.util.List;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Rule extends Syntax {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    Selector selector();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<Declaration> declarations();
}
