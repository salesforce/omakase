/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

/**
 * Generic parent for all Omakase CSS Parser related exceptions.
 * 
 * @author nmcwilliams
 */
public class OmakaseException extends RuntimeException {
    private static final long serialVersionUID = -2367547081410118208L;

    /**
     * TODO
     * 
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
