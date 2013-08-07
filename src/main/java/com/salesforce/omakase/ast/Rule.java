/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

/**
 * Represents a CSS Rule. Each rule has one or more {@link Selector}s and zero or more {@link Declaration}s.
 * 
 * @author nmcwilliams
 */
public interface Rule extends Statement, Linkable<Rule> {
    /**
     * TODO Description
     * 
     * @param selector
     *            TODO
     * @return TODO
     */
    Rule selector(Selector selector);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<Selector> selectors();

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     * @return TODO
     */
    Rule declaration(Declaration declaration);

    /**
     * Gets the {@link Declaration}s within this {@link Rule}. May be empty.
     * 
     * @return The {@link Declaration}s.
     */
    List<Declaration> declarations();
}
