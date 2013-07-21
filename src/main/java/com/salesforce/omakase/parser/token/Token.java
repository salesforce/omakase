/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.token;

import com.google.common.base.CharMatcher;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Token {

    /**
     * TODO Description
     * 
     * @return TODO
     */
    CharMatcher matcher();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    String description();

    /**
     * TODO Description
     * 
     * @param c
     *            TODO
     * @return TODO
     */
    boolean matches(Character c);

    /**
     * TODO Description
     * 
     * @param other
     * @return TODO
     */
    Token or(Token other);

}
