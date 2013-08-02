/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Errors;

/**
 * An error encountered while parsing.
 * 
 * @see Errors
 * 
 * @author nmcwilliams
 */
public class ParserException extends RuntimeException {
    private static final long serialVersionUID = -8952238331167900360L;

    /**
     * Construct a new instance of a {@link ParserException}.
     * 
     * @param stream
     *            The stream containing the source of the error.
     * @param msg
     *            The error message.
     */
    public ParserException(Stream stream, String msg) {
        super(msg + indicator(stream.line(), stream.column(), stream.source()));
    }

    private static String indicator(int line, int column, String source) {
        return String.format("\n at line %s, column %s in '%s", line, column, source);
    }
}
