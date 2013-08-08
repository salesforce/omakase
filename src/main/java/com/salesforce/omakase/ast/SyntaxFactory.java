/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SyntaxFactory {
    /**
     * TODO Description
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param original
     *            TODO
     * @return TODO
     */
    Selector selector(int line, int column, String original);

    Declaration declaration(int line, int column, String original);
}
