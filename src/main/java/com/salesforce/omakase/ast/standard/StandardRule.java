/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.standard;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Selector;

/**
 * Standard implementation of a {@link Rule}.
 * 
 * <p> Not intended for subclassing or direct reference by clients.
 * 
 * @author nmcwilliams
 */
final class StandardRule extends AbstractLinkableSyntax<Rule> implements Rule {
    private final List<Selector> selectors = Lists.newArrayList();
    private final List<Declaration> declarations = Lists.newArrayList();

    StandardRule(int line, int column, Iterable<Selector> selectors, Iterable<Declaration> declarations) {
        super(line, column);
        Iterables.addAll(this.selectors, selectors);
        Iterables.addAll(this.declarations, declarations);
    }

    @Override
    public Rule selector(Selector selector) {
        selectors.add(selector);
        return this;
    }

    @Override
    public List<Selector> selectors() {
        return ImmutableList.copyOf(selectors);
    }

    @Override
    public Rule declaration(Declaration declaration) {
        this.declarations.add(declaration);
        return this;
    }

    @Override
    public List<Declaration> declarations() {
        return ImmutableList.copyOf(declarations);
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder(32);
        sBuilder.append("\n  ");
        for (Selector selector : selectors) {
            sBuilder.append(selector);
        }

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
