/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

/**
 * Describes the level of severity of a problem reported to an {@link ErrorManager}.
 * 
 * @author nmcwilliams
 */
public enum ErrorLevel {
    /** A non-fatal warning */
    WARNING,
    /** A fatal error */
    FATAL,
    /** the error should be ignored */
    IGNORE
}
