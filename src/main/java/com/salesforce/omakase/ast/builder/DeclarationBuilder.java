/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * A {@link Builder} used to create {@link Declaration} instances.
 * 
 * @author nmcwilliams
 */
public interface DeclarationBuilder extends Builder<Declaration> {
    /**
     * Specifies the content of the selector. For example, "color: red". This should include the full declaration, but
     * should <em>not</em> include the declaration delimiter, e.g., ";".
     * 
     * <p> The content can also contain CSS comments (usually at the beginning).
     * 
     * @param content
     *            The content of the selector.
     * @return this, for chaining.
     */
    DeclarationBuilder content(String content);
}
