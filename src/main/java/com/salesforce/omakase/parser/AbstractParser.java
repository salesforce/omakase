/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.SyntaxFactory;
import com.salesforce.omakase.ast.impl.StandardSyntaxFactory;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class AbstractParser implements Parser {
    private final SyntaxFactory factory = new StandardSyntaxFactory();

    /**
     * TODO Description
     * 
     * @param other
     *            TODO
     * @return TODO
     */
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }

    protected SyntaxFactory factory() {
        return factory;
    }
}
