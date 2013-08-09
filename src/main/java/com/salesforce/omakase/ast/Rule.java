/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.salesforce.omakase.LinkableIterator;

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
     * @param selectorGroup
     *            TODO
     * @param head
     *            TODO
     */
    protected Rule(SelectorGroup selectorGroup, Declaration head) {
        super(selectorGroup.line(), selectorGroup.column());
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
    public SelectorGroup selectorGroup() {
        return selectorGroup;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Iterator<Declaration> declarations() {
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("selectorGroup", selectorGroup)
            .add("declarations", declarations())
            .toString();
    }
}
