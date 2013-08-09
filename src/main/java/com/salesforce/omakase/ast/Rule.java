/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.salesforce.omakase.LinkableIterator;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * Represents a CSS Rule. Each rule has one or more {@link Selector}s and zero or more {@link Declaration}s.
 * 
 * @author nmcwilliams
 */
public class Rule extends AbstractLinkableSyntax<Statement> implements Statement {
    private final SelectorGroup selectorGroup;
    private final Declaration head;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param selectorGroup
     *            TODO
     * @param head
     *            TODO
     */
    protected Rule(int line, int column, SelectorGroup selectorGroup, Declaration head) {
        super(line, column);
        this.selectorGroup = selectorGroup;
        this.head = head;
    }

    @Override
    protected Statement get() {
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorGroup selectorGroup() {
        return selectorGroup;
    }

    Iterator<Declaration> declarations() {
        return LinkableIterator.create(head);
    }

    /**
     * TODO Description
     * 
     * <p> Avoid if possible, as this method is less efficient. Prefer instead to append the declaration directly to a
     * specific instance of an existing one.
     * 
     * @param declaration
     *            TODO
     * @return this, for chaining.
     */
    public Rule appendDeclaration(Declaration declaration) {
        Iterators.getLast(declarations()).append(declaration);
        return this;
    }

}
