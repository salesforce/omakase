/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.error.OmakaseException;

/**
 * An error that occurs while invoking a method annotated with {@link Rework}.
 * 
 * @author nmcwilliams
 */
public class SubscriptionException extends OmakaseException {
    private static final long serialVersionUID = 7730100425922298149L;

    /**
     * @param message
     *            The error message.
     */
    public SubscriptionException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The error message.
     * @param cause
     *            The underlying cause.
     */
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
