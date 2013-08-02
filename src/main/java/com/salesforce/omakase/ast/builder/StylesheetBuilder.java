/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * A {@link Builder} used to create {@link Stylesheet} instances.
 * 
 * @author nmcwilliams
 */
public interface StylesheetBuilder extends Builder<Stylesheet> {
    /**
     * Adds a {@link Statement} to the list.
     * 
     * @param statement
     *            The statement to add.
     * @return this, for chaining.
     */
    StylesheetBuilder statement(Statement statement);
}
