/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import java.io.IOException;


/**
 * Indicates that something can be written to a {@link StyleAppendable}.
 * 
 * @author nmcwilliams
 */
public interface Writable {
    /**
     * Outputs this {@link Writable}'s string representation.
     * 
     * <p>
     * <b>Important notes for implementations:</b>
     * 
     * <p>
     * Do not use the {@link StyleWriter} in an attempt to write direct content (Strings, chars, etc...). Use the
     * {@link StyleAppendable}.
     * 
     * <p>
     * The {@link StyleWriter} should be used to make decisions based on writer settings (e.g., compressed vs. verbose
     * output mode), as well as for writing inner or child {@link Writable}s. Do <b>not</b> call the
     * {@link #write(StyleWriter, StyleAppendable)} method directly on inner or child {@link Writable}s! That would
     * bypass any overrides that are set on the {@link StyleWriter}.
     * 
     * @param writer
     *            Writer to use for output settings and for writing inner {@link Writable}s.
     * @param appendable
     *            Append direct content to this {@link StyleAppendable}.
     * @throws IOException
     *             If an I/O error occurs.
     */
    void write(StyleWriter writer, StyleAppendable appendable) throws IOException;
}
