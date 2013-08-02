/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * A factory used to obtain {@link Builder}s for constructing {@link Syntax} object instances.
 * 
 * @author nmcwilliams
 */
public interface SyntaxFactory {
    /**
     * Gets a {@link Builder} for constructing a new {@link Stylesheet}.
     * 
     * @return this, for chaining.
     */
    StylesheetBuilder stylesheet();

    /**
     * Gets a {@link Builder} for constructing a new {@link Rule}.
     * 
     * @return this, for chaining.
     */
    RuleBuilder rule();

    /**
     * Gets a {@link Builder} for constructing a new {@link SelectorGroup}.
     * 
     * @return this, for chaining.
     */
    SelectorGroupBuilder selectorGroup();

    /**
     * Gets a {@link Builder} for constructing a new {@link Selector}.
     * 
     * @return this, for chaining.
     */
    SelectorBuilder selector();

    /**
     * Gets a {@link Builder} for constructing a new {@link Declaration}.
     * 
     * @return this, for chaining.
     */
    DeclarationBuilder declaration();
}
