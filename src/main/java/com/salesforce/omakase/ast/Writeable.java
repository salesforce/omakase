/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.io.IOException;

import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Writeable {
    /**
     * TODO Description
     * 
     * @param writer
     *            TODO
     * @param appendable
     *            TODO
     * @throws IOException
     *             TODO
     */
    void write(StyleWriter writer, StyleAppendable appendable) throws IOException;
}
