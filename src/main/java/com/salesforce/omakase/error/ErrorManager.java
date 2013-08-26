/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.plugin.validator.SyntaxValidator;

/**
 * Responsible for handling errors, either from {@link ParserException}s or from {@link SyntaxValidator}s.
 * 
 * <p>
 * As a general note, <em>some</em> {@link ParserException}s (of id {@link ErrorId#PARSING}) are non-recoverable, so
 * trying to swallow or collect them for later may not work out too well. However usually all {@link SyntaxValidator}s
 * are recoverable.
 * 
 * @see ThrowingErrorManager
 * 
 * @author nmcwilliams
 */
public interface ErrorManager {
    /**
     * Reports an error.
     * 
     * <p>
     * Each error id is a string, as specified by the {@link SyntaxValidator} reporting the error, usually in the form
     * of "x.y", e.g., "omakase.pseudoElementPosition".
     * 
     * @param id
     *            The error id.
     * @param defaultLevel
     *            The default {@link ErrorLevel}.
     * @param message
     *            The error message.
     */
    void report(String id, ErrorLevel defaultLevel, String message);

    /**
     * Specifies a {@link Reporting} instance for overriding error levels.
     * 
     * @param reporting
     *            The {@link Reporting} instance.
     */
    void reporting(Reporting reporting);
}
