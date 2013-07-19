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

    CharMatcher matcher();

    String description();

    /**
     * TODO Description
     * 
     * @param c
     *            TODO
     * @return TODO
     */
    boolean matches(Character c);

    Token or(Token other);

}