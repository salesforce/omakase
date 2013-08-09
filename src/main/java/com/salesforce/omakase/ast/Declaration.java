/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

/**
 * A CSS declaration, comprised of a property and value.
 * 
 * @author nmcwilliams
 */
public class Declaration extends AbstractLinkableSyntax<Declaration> implements Refinable<RefinedDeclaration>,
        RefinedDeclaration {
    private final String raw;

    /**
     * @param line
     * @param column
     */
    public Declaration(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    @Override
    protected Declaration get() {
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String raw() {
        return raw;
    }

    @Override
    public Property property() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String value() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RefinedDeclaration refine() {
        // TODO Auto-generated method stub
        return null;
    }
}
