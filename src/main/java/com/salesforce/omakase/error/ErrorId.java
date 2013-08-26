/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

/**
 * Collection of error ids, for reporting to {@link ErrorManager}s.
 * 
 * @author nmcwilliams
 */
public final class ErrorId {
    /** do not construct */
    private ErrorId() {}

    /** general parsing errors */
    public static final String PARSING = "omakase.parsing";
}
