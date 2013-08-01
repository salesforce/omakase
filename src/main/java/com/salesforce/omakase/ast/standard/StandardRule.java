/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import static com.salesforce.omakase.Util.immutable;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
final class StandardRule extends AbstractSyntax implements Rule {
    private final SelectorGroup selectorGroup;
    private final ImmutableList<Declaration> declarations;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param comments
     *            TODO
     * @param selectorGroup
     *            TODO
     * @param declarations
     *            TODO
     */
    public StandardRule(int line, int column, List<String> comments, SelectorGroup selectorGroup, List<Declaration> declarations) {
        super(line, column, comments);
        this.selectorGroup = selectorGroup;
        this.declarations = immutable(declarations);
    }

    @Override
    public SelectorGroup selectorGroup() {
        return selectorGroup;
    }

    @Override
    public List<Declaration> declarations() {
        return declarations;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("super", super.toString())
            .add("selectorGroup", selectorGroup)
            .add("declarations", declarations)
            .toString();
    }
}
