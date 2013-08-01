/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.builder.SyntaxFactory;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.ast.standard.StandardSyntaxFactory;
import com.salesforce.omakase.observer.Observer;

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

    /**
     * TODO Description
     * 
     * @param declaration
     *            TODO
     * @param observers
     *            TODO
     */
    protected void announce(Declaration declaration, Iterable<Observer> observers) {
        for (Observer observer : observers) {
            observer.declaration(declaration);
        }
    }

    /**
     * TODO Description
     * 
     * @param selectorGroup
     *            TODO
     * @param observers
     *            TODO
     */
    protected void announce(SelectorGroup selectorGroup, Iterable<Observer> observers) {
        for (Observer observer : observers) {
            observer.selectorGroup(selectorGroup);
        }
    }
}
