/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * TESTME The generic and default {@link Declaration}'s {@link PropertyValue}. This contains a list of {@link Term}s,
 * for example numbers, keywords, functions, hex colors, etc...
 * 
 * <p>
 * If you need to change the contents of the {@link TermList}, change the contents of the actual {@link Term} itself. If
 * you need to remove or add {@link Term}s from the {@link TermList}, create a new {@link TermList} to replace this one
 * with instead. (ACTUALLY I'm not sure why this comment is here. maybe it can be ignored).
 * 
 * <p>
 * In the CSS 2.1 spec this is called "expr", which is obviously shorthand for "expression", however "expression" is
 * name now given to multiple syntax units within different CSS3 modules! So that's why this is not called expression.
 * 
 * XXX This setup is perhaps inconsistent with the rest of the project, with respect to the term members being directly
 * added instead of broadcasted. Also, as noted above, this doesn't allow for additions/removals from the list, which
 * would be nice to support.
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
    private final List<TermListMember> members = Lists.newArrayListWithCapacity(4);

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
     * TODO
     */
    public TermList() {}

    /**
     * Adds a {@link TermListMember}.
     * 
     * @param member
     *            The member to add.
     * @return this, for chaining.
     */
    public TermList add(TermListMember member) {
        this.members.add(member);
        return this;
    }

    /**
     * Gets a list of all {@link TermListMember}s in this list.
     * 
     * @return All {@link TermListMember}s.
     */
    public ImmutableList<TermListMember> members() {
        return ImmutableList.copyOf(members);
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        for (TermListMember member : members) {
            // FIXME
            if (member instanceof Term) {
                ((Term)member).propagateBroadcast(broadcaster);
            }
        }
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        for (TermListMember member : members) {
            writer.write(member, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("members", members)
            .toString();
    }

    /**
     * TODO Description
     * 
     * @param term
     *            TODO
     * @return TODO
     */
    public static PropertyValue singleValue(Term term) {
        return new TermList(-1, -1).add(term);
    }

    /**
     * TODO Description
     * 
     * @param separator
     *            TODO
     * @param values
     *            TODO
     * @return TODO
     */
    public static PropertyValue ofValues(TermOperator separator, Term... values) {
        TermList termList = new TermList();
        for (int i = 0; i < values.length; i++) {
            if (i != 0) termList.add(separator);
            termList.add(values[i]);
        }
        return termList;
    }
}
