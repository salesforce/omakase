/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.declaration.PropertyValue;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class TermList extends AbstractSyntax implements PropertyValue {
    private final List<TermMember> terms = Lists.newArrayListWithCapacity(4);

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public TermList(int line, int column) {
        super(line, column);
    }

    /**
     * TODO Description
     * 
     * @param term
     *            TODO
     * @return TODO
     */
    public TermList add(TermMember term) {
        this.terms.add(term);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public ImmutableList<TermMember> terms() {
        return ImmutableList.copyOf(terms);
    }

    @Override
    public String toString() {
        return As.string(this).indent()
            .add("syntax", super.toString())
            .add("terms", terms)
            .toString();
    }
}
