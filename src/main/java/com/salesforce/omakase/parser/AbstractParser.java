/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.standard.StandardSyntaxFactory;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class AbstractParser implements Parser {
    private final SyntaxFactory factory;

    /**
     * TODO
     */
    public AbstractParser() {
        this(StandardSyntaxFactory.instance());
    }

    /**
     * TODO
     * 
     * @param factory
     *            TODO
     */
    public AbstractParser(SyntaxFactory factory) {
        this.factory = factory;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    protected SyntaxFactory factory() {
        return factory;
    }

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
}
