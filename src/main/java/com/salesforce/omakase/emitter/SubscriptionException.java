/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.error.OmakaseException;
import com.salesforce.omakase.parser.ParserException;

/**
 * An error that occurs while invoking a subscription method.
 *
 * @author nmcwilliams
 */
public class SubscriptionException extends OmakaseException {
    private static final long serialVersionUID = 7730100425922298149L;

    /**
     * Construct a new instance of a {@link ParserException} with the given {@link Message} and message parameters.
     *
     * @param message
     *     The error message.
     * @param args
     *     The {@link String#format(String, Object...)} parameters to pass to {@link Message#message(Object...)}.
     */
    public SubscriptionException(Message message, Object... args) {
        this(message.message(args));
    }

    /**
     * @param message
     *     The error message.
     */
    public SubscriptionException(String message) {
        super(message);
    }

    /**
     * @param message
     *     The error message.
     * @param cause
     *     The underlying cause.
     */
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
