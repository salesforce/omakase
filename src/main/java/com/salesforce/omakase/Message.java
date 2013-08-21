/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

/**
 * Error messages.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public enum Message {
    UNEXPECTED_CONTENT("Unable to parse remaining selector content (Check that the selector is valid and is allowed here)"),
    MISSING_PSEUDO_NAME("expected to find a valid pseudo element or class name ([-_0-9a-zA-Z], cannot start with a number)");

    private String message;

    Message(String message) {
        this.message = message;
    }

    /**
     * Gets the error message. If the message contains parameters for {@link String#format(String, Object...)} , use
     * {@link #message(String...)} instead.
     */
    public String message() {
        return message;
    }

    /**
     * Gets the error message, passing in the given arguments to {@link String#format(String, Object...)}.
     * 
     * @param parameters
     *            Arguments to {@link String#format(String, Object...)}.
     * @return
     */
    public String message(Object... parameters) {
        return String.format(message, parameters);
    }
}
