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
import com.salesforce.omakase.parser.declaration.ExpressionParser;

/**
 * TODO Description
 * 
 * @see ExpressionParser
 * @see ExpressionTerm
 * 
 * @author nmcwilliams
 */
public class Expression extends AbstractSyntax implements PropertyValue {
    private final List<ExpressionMember> terms = Lists.newArrayListWithCapacity(4);

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public Expression(int line, int column) {
        super(line, column);
    }

    /**
     * TODO Description
     * 
     * @param term
     *            TODO
     * @return TODO
     */
    public Expression add(ExpressionMember term) {
        this.terms.add(term);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public ImmutableList<ExpressionMember> terms() {
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
