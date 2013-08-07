/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import com.salesforce.omakase.ast.Selector;
import com.salesforce.omakase.ast.SyntaxFactory;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxFactory implements SyntaxFactory {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static SyntaxFactory instance() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Selector selector(int line, int column, String original) {
        return new StandardSelector(line, column, original);
    }
}
