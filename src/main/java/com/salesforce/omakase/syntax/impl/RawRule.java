/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class RawRule extends AbstractSyntaxUnit implements Rule {
    private Selector selector;
    private List<Declaration> declarations;

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     */
    public RawRule(int line, int column) {
        super(line, column);
    }

    @Override
    public RefinedRule refine() {
        return new RefinedRule(this);
    }

    @Override
    public Rule selector(Selector selector) {
        this.selector = checkNotNull(selector, "selector cannot be null");
        return this;
    }

    @Override
    public Selector selector() {
        return selector;
    }

    @Override
    public Rule declaration(Declaration declaration) {
        if (declarations == null) {
            declarations = Lists.newArrayList();
        }
        declarations.add(declaration);
        return this;
    }

    @Override
    public List<Declaration> declarations() {
        return declarations;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("selector", selector)
            .add("declarations", declarations)
            .toString();
    }
}
