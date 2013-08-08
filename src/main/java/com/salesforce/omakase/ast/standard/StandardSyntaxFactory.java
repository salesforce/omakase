/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Selector;
import com.salesforce.omakase.ast.SyntaxFactory;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxFactory implements SyntaxFactory {
    private static final SyntaxFactory instance = new StandardSyntaxFactory();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static SyntaxFactory instance() {
        return instance;
    }

    @Override
    public Selector selector(int line, int column, String original) {
        return new StandardSelector(line, column, original);
    }

    @Override
    public Declaration declaration(int line, int column, String original) {
        return new StandardDeclaration(line, column, original);
    }
}
