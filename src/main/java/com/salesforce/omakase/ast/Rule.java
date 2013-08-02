/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * Represents a CSS Rule. Each rule has one {@link SelectorGroup} and any number of ordered {@link Declaration}s.
 * 
 * @author nmcwilliams
 */
public interface Rule extends Statement {
    /**
     * Gets the {@link SelectorGroup} containing the {@link Selector}s for this {@link Rule}.
     * 
     * @return The {@link SelectorGroup}.
     */
    SelectorGroup selectorGroup();

    /**
     * Gets the {@link Declaration}s within this {@link Rule}. May be empty.
     * 
     * @return The {@link Declaration}s.
     */
    List<Declaration> declarations();
}
