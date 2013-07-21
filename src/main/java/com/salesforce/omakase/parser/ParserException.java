/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class ParserException extends RuntimeException {
    private static final long serialVersionUID = -8952238331167900360L;

    /**
     * TODO
     * 
     * @param stream
     *            TODO
     * @param msg
     *            TODO
     */
    public ParserException(Stream stream, String msg) {
        super(msg + indicator(stream.line(), stream.column(), stream.source()));
    }

    private static String indicator(int line, int column, String source) {
        return String.format("\n at line %s, column %s in '%s", line, column, source);
    }
}
