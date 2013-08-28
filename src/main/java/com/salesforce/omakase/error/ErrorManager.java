/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.parser.ParserException;

/**
 * Responsible for handling errors, either from {@link ParserException}s or from syntax validator plugins.
 * 
 * @author nmcwilliams
 */
public interface ErrorManager {
    /**
     * Reports an error based on a {@link ParserException}.
     * 
     * @param level
     *            The {@link ErrorLevel}.
     * @param exception
     *            The exception that describes the error.
     */
    void report(ErrorLevel level, ParserException exception);

    /**
     * Reports an error message.
     * 
     * @param level
     *            The {@link ErrorLevel}.
     * @param cause
     *            The {@link Syntax} unit that is the cause of the error (e.g., the unit that has failed validation).
     * @param message
     *            The error message.
     */
    void report(ErrorLevel level, Syntax cause, Message message);

    /**
     * Reports an error message.
     * 
     * @param level
     *            The {@link ErrorLevel}.
     * @param cause
     *            The {@link Syntax} unit that is the cause of the error (e.g., the unit that has failed validation).
     * @param message
     *            The error message.
     */
    void report(ErrorLevel level, Syntax cause, String message);
}
