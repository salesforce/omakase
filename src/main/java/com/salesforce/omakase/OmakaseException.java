/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class OmakaseException extends RuntimeException {
    private static final long serialVersionUID = -2367547081410118208L;

    /**
     * @param message
     *            TODO
     */
    public OmakaseException(String message) {
        super(format(message));
    }

    /**
     * @param message
     *            TODO
     * @param cause
     *            TODO
     */
    public OmakaseException(String message, Throwable cause) {
        super(format(message), cause);
    }

    private static String format(String message) {
        return String.format("Omakase CSS Parser: %s", message);
    }
}
