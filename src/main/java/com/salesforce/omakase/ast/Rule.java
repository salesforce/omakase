/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.standard.SyntaxTree;

/**
 * Represents a CSS Rule. Each rule has one {@link SelectorGroup}s and one or more {@link Declaration}s.
 * 
 * <p>
 * Note that {@link Rule}s will not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * <p>
 * Note that the {@link SelectorGroup} cannot be changed, however you can freely add and remove {@link Selector}s from
 * the group.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(broadcasted = SYNTAX_TREE)
public class Rule extends AbstractLinkable<Statement> implements Statement {
    private final SelectorGroup selectorGroup;
    private final Declaration declarationHead;

    /**
     * Creates a new {@link Rule} instance.
     * 
     * @param selectorGroup
     *            The {@link SelectorGroup} instance.
     * @param declarationHead
     *            The first {@link Declaration} in the rule.
     */
    public Rule(SelectorGroup selectorGroup, Declaration declarationHead) {
        super(selectorGroup.line(), selectorGroup.column());
        this.selectorGroup = selectorGroup;
        this.declarationHead = declarationHead;
    }

    /**
     * Gets the {@link SelectorGroup}.
     * 
     * @return The {@link SelectorGroup}.
     */
    public SelectorGroup selectorGroup() {
        return selectorGroup;
    }

    /**
     * Gets the {@link Declaration}s. Note that moving forward and backwards from a specific {@link Declaration}
     * instance is generally more preferred.
     * 
     * @return A {@link LinkableCollection} of the {@link Declaration}s.
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
