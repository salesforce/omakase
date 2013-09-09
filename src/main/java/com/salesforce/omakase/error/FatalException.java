/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

/**
 * An exception indicating that an unrecoverable, fatal error has occurred, upon which processing should immediately be halted.
 *
 * @author nmcwilliams
 */
public final class FatalException extends OmakaseException {
    private static final long serialVersionUID = 2723924358238169904L;

    /**
     * Creates a new exception with the given message.
     *
     * @param message
     *     The description of the exception.
     */
    protected FatalException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message
     *     The description of the exception.
     * @param cause
     *     The cause of the exception.
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
