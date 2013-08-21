/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.declaration.TermListParser;

/**
 * The generic and default {@link Declaration}'s {@link PropertyValue}. This contains a list of {@link Term}s, for
 * example numbers, keywords, functions, hex colors, etc...
 * 
 * <p>
 * If you need to change the contents of a the {@link TermList}, change the contents of the actual {@link Term} itself.
 * If you need to remove or add {@link Term}s from the {@link TermList}, create a new {@link TermList} to replace this
 * one with instead.
 * 
 * <p>
 * In the CSS 2.1 spec this is called "expr", which is obviously shorthand for "expression", however "expression" is
 * name now given to multiple syntax units within different CSS3 modules! So that's why this is not called expression.
 * 
 * @see Term
 * @see TermListParser
 * @see TermListMember
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "default, generic property value", broadcasted = REFINED_DECLARATION)
public class TermList extends AbstractSyntax implements PropertyValue {
    private final List<TermListMember> terms = Lists.newArrayListWithCapacity(4);

    /**
     * Constructs a new {@link TermList} instance.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public TermList(int line, int column) {
        super(line, column);
    }

    /**
     * Adds a {@link TermListMember}.
     * 
     * @param member
     *            The member to add.
     * @return this, for chaining.
     */
    public TermList add(TermListMember member) {
        this.terms.add(member);
        return this;
    }

    /**
     * Gets a list of all {@link TermListMember}s in this list.
     * 
     * @return All {@link TermListMember}s.
     */
    public ImmutableList<TermListMember> terms() {
        return ImmutableList.copyOf(terms);
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("syntax", super.toString())
            .add("terms", terms)
            .toString();
    }
}
