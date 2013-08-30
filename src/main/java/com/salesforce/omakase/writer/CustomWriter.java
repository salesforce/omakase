/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import java.io.IOException;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.Writable;

/**
 * TESTME Customizes the writing of a particular {@link Syntax} unit.
 * 
 * <p>
 * This allows you to override (or augment) the writing of any {@link Syntax} unit. For example, to check the comments
 * for a directive that dictates how the unit should be written.
 * 
 * @param <T>
 *            The Type of object being overridden.
 * @author nmcwilliams
 */
public interface CustomWriter<T extends Writable> {
    /**
     * Writes the given unit to the given {@link StyleAppendable}.
     * 
     * <p>
     * <b>Notes for implementation:</b>
     * 
     * <p>
     * You can completely bypass the default writing behavior of the unit by simply writing out content to the
     * {@link StyleAppendable}.
     * 
     * <p>
     * If you are augmenting the write process instead, you can output the default representation of the unit by calling
     * {@link StyleWriter#write(Writable, StyleAppendable)} before or after your augmentations, as appropriate.
     * 
     * <p>
     * Do not use the {@link StyleWriter} in an attempt to write direct content (Strings, chars, etc...). Use the
     * {@link StyleAppendable}.
     * 
     * <p>
     * The {@link StyleWriter} should be used to make decisions based on writer settings (e.g., compressed vs. verbose
     * output mode), as well as for writing inner or child {@link Writable}s.
     * 
     * @param unit
     *            The unit to write.
     * @param writer
     *            Writer to use for output settings and for writing inner {@link Writable}s.
     * @param appendable
     *            Append direct content to this {@link StyleAppendable}.
     * @throws IOException
     *             If an I/O error occurs.
     */
    void write(T unit, StyleWriter writer, StyleAppendable appendable) throws IOException;
}
