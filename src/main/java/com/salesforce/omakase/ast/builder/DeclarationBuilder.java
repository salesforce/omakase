/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.declaration.Declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface DeclarationBuilder extends Builder<Declaration> {
    /**
     * TODO Description
     * 
     * @param property
     *            TODO
     * @return TODO
     */
    DeclarationBuilder property(String property);

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    DeclarationBuilder value(String value);
}
