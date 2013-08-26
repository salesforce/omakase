/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

/**
 * Generic parent for all Omakase CSS Parser related exceptions.
 * 
 * @author nmcwilliams
 */
public class OmakaseException extends RuntimeException {
    private static final long serialVersionUID = -2367547081410118208L;

    /**
     * Creates a new exception with the given message.
     * 
     * @param message
     *            The description of the exception.
     */
    protected OmakaseException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     * 
     * @param message
     *            The description of the exception.
     * @param cause
     *            The cause of the exception.
     */
    protected OmakaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
