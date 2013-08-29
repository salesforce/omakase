/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import com.salesforce.omakase.ast.Writeable;

/**
 * TODO Description
 * 
 * @param <T>
 *            TODO
 * @author nmcwilliams
 */
public interface CustomWriter<T extends Writeable> {
    /**
     * TODO Description
     * 
     * @param unit
     *            TODO
     * @param builder
     *            TODO
     */
    void write(T unit, StyleAppendable builder);
}
