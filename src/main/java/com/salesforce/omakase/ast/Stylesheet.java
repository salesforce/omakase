/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * The root-level {@link Syntax} object. This contains directly and indirectly the {@link Rule}s, {@link Selector}s,
 * {@link Declaration}s, etc.. of a parsed CSS resource.
 * 
 * @author nmcwilliams
 */
public interface Stylesheet extends Syntax {
    /**
     * Gets all of the {@link Statement}s within this {@link Stylesheet}.
     * 
     * @return The {@link Statement}s.
     */
    List<Statement> statements();
}
