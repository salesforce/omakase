/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;

/**
 * Error messages.
 * 
 * @author nmcwilliams
 */
public enum Errors {
    /** didn't match the expected character */
    expected("Expected to find '%s'"),
    /** extra input at the end of the source that doesn't match a rule or at-rule */
    extraneous("Extraneous text found at the end of the source '%s'")

    ;

    private final String template;

    Errors(String template) {
        this.template = template;
    }

    /**
     * Creates and throws an exception for this error message.
     * 
     * @param stream
     *            The stream containing the source of the error.
     * @param args
     *            Any arguments to String.format, as applicable to the particular error message.
     */
    public void send(Stream stream, Object... args) {
        throw new ParserException(stream, String.format(template, args));
    }
}
