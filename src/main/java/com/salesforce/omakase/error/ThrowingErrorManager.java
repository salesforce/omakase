/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.parser.ParserException;

/**
 * TESTME An {@link ErrorManager} that will throw an exception on the first reported {@link ErrorLevel#FATAL} error.
 * Errors of level {@link ErrorLevel#WARNING} will be logged.
 * 
 * @author nmcwilliams
 */
public final class ThrowingErrorManager implements ErrorManager {
    private static final Logger logger = LoggerFactory.getLogger(ErrorManager.class);

    @Override
    public void report(ErrorLevel level, ParserException exception) {
        switch (level) {
        case FATAL:
            throw new FatalException(format(exception.getMessage()), exception);
        case WARNING:
            logger.warn(format(exception.getMessage()), exception);
        }
    }

    @Override
    public void report(ErrorLevel level, Syntax cause, Message message) {
        report(level, cause, message.message());

    }

    @Override
    public void report(ErrorLevel level, Syntax cause, String message) {
        switch (level) {
        case FATAL:
            throw new FatalException(format(message, cause));
        case WARNING:
            logger.warn(format(message, cause));
        }
    }

    /** formats the error message */
    private static String format(String message) {
        return String.format("Omakase CSS Parser - %s", message);
    }

    private static String format(String message, Syntax cause) {
        return String.format("Omakase CSS Parser - %s \n cause: %s \n at line %s, column %s.",
            message,
            cause.toString(),
            cause.line(),
            cause.column());
    }
}
