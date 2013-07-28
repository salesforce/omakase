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
public interface Syntax {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    int line();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    int column();

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
     * @return TODO
     */
    List<String> comments();
}
