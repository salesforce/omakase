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
 * Standard implementation of a {@link Rule}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardRule extends AbstractSyntax implements Rule {
    private final SelectorGroup selectorGroup;
    private final ImmutableList<Declaration> declarations;

    StandardRule(int line, int column, SelectorGroup selectorGroup, List<Declaration> declarations) {
        super(line, column);
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
        StringBuilder sBuilder = new StringBuilder(32);
        sBuilder.append("\n  ").append(selectorGroup);

        StringBuilder dBuilder = new StringBuilder(128);
        for (Declaration declaration : declarations) {
            dBuilder.append("\n  ").append(declaration);
        }

        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .addValue(sBuilder.toString())
            .addValue(dBuilder.toString())
            .toString();
    }
}
