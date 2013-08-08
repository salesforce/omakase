/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.OmakaseException;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SubscriptionException extends OmakaseException {
    private static final long serialVersionUID = 7730100425922298149L;

    /**
     * @param message
     *            TODO
     */
    public SubscriptionException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     *            TODO
     * @param cause
     *            TODO
     */
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
