/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * Indicates the conditions for an {@link Subscribable} type to be broadcasted.
 * 
 * @author nmcwilliams
 */
public enum SubscribableRequirement {
    /** automatically broadcasted (except for Syntax, where some syntax items are only broadcasted during refinement) */
    AUTOMATIC("Automatic"),

    /** requires the {@link SyntaxTree} plugin */
    SYNTAX_TREE("SyntaxTree"),

    /** Refine must be called on {@link Selector}s, e.g., with {@link AutoRefiner#selectors()} */
    REFINED_SELECTOR("Selector#refine"),

    /** Refine must be called on {@link Declaration}s, e.g., with {@link AutoRefiner#declarations()} */
    REFINED_DECLARATION("Declaration#refine");

    private final String description;

    private SubscribableRequirement(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
