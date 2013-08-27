/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ErrorManager} that will throw an exception on the first reported {@link ErrorLevel#FATAL} error. Errors of
 * level {@link ErrorLevel#WARNING} will be logged.
 * 
 * @author nmcwilliams
 */
public final class ThrowingErrorManager implements ErrorManager {
    private static final Logger logger = LoggerFactory.getLogger(ErrorManager.class);

    @Override
    public void report(ErrorLevel level, String message) {
        switch (level) {
        case FATAL:
            throw new FatalOmakaseException(format(message));
        case WARNING:
            logger.warn(format(message));
        }
    }

    /** formats the error message */
    private static String format(String message) {
        return String.format("Omakase CSS Parser Fatal Error: %s", message);
    }
}
