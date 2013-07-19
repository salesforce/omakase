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
     * @param msg
     *            TODO
     * @param stream
     *            TODO
     */
    public ParserException(String msg, Stream stream) {
        this(msg, stream.line(), stream.column(), stream.source());
    }

    public ParserException(String msg, int line, int column, String source) {
        super(msg + indicator(line, column, source));
    }

    private static String indicator(int line, int column, String source) {
        return String.format("\n at line %s, column %s in '%s", line, column, source);
    }
}
