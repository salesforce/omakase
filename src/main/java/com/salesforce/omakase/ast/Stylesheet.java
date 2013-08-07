/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

/**
 * The root-level {@link Syntax} object. This contains directly and indirectly the {@link Rule}s, {@link Selector}s,
 * {@link Declaration}s, etc.. of a parsed CSS resource.
 * 
 * @author nmcwilliams
 */
public interface Stylesheet extends Syntax {
    /**
     * TODO Description
     * 
     * @param statement
     *            TODO
     * @return TODO
     */
    Stylesheet statement(Statement statement);

    /**
     * Gets all of the {@link Statement}s within this {@link Stylesheet}.
     * 
     * @return The {@link Statement}s.
     */
    List<Statement> statements();
}
