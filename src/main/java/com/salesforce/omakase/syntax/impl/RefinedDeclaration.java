/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.salesforce.omakase.syntax.Declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class RefinedDeclaration extends AbstractSyntaxUnit implements Declaration {

    /**
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public RefinedDeclaration(int line, int column) {
        super(line, column);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param rawDeclaration
     *            TODO
     */
    public RefinedDeclaration(RawDeclaration rawDeclaration) {
        super(rawDeclaration.line(), rawDeclaration.column());
    }

    @Override
    public RefinedDeclaration refine() {
        return this;
    }

    @Override
    public String property() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String value() {
        // TODO Auto-generated method stub
        return null;
    }

}
