/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.As;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.SyntaxTree;

/**
 * Represents a CSS Rule. Each rule has one {@link SelectorGroup}s and zero or more {@link Declaration}s.
 * 
 * <p>
 * Note that this will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Subscribable
public class Rule extends AbstractLinkable<Statement> implements Statement {
    private final SelectorGroup selectorGroup;
    private final Declaration declarationHead;

    /**
     * TODO
     * 
     * @param selectorGroup
     *            TODO
     * @param declarationHead
     *            TODO
     */
    public Rule(SelectorGroup selectorGroup, Declaration declarationHead) {
        super(selectorGroup.line(), selectorGroup.column());
        this.selectorGroup = selectorGroup;
        this.declarationHead = declarationHead;
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
    public LinkableCollection<Declaration> declarations() {
        return LinkableCollection.of(declarationHead);
    }

    /**
     * TODO Description
     * 
     * <p>
     * Avoid if possible, as this method is less efficient. Prefer instead to append the declaration directly to a
     * specific instance of an existing one.
     * 
     * @param declaration
     *            TODO
     * @return this, for chaining.
     */
    public Rule appendDeclaration(Declaration declaration) {
        Iterables.getLast(declarations()).append(declaration);
        return this;
    }

    @Override
    protected Statement self() {
        return this;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("selectorGroup", selectorGroup)
            .add("declarations", declarations())
            .toString();
    }
}
