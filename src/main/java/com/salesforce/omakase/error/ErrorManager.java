/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.salesforce.omakase.parser.ParserException;

/**
 * Responsible for handling errors, either from {@link ParserException}s or from syntax validator plugins.
 * 
 * @author nmcwilliams
 */
public interface ErrorManager {
    /**
     * Reports an error.
     * 
     * @param level
     *            The {@link ErrorLevel}.
     * @param message
     *            The error message.
     */
    void report(ErrorLevel level, String message);
}
