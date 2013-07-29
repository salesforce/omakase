/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.impl;

import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StandardSyntaxFactory implements SyntaxFactory {
    @Override
    public Stylesheet stylesheet(int line, int column) {
        return new StandardStylesheet();
    }

    @Override
    public Rule rule(int line, int column) {
        return new StandardRule(line, column);
    }

    @Override
    public Declaration declaration(int line, int column, String property, String value) {
        return new StandardDeclaration(line, column, property, value);
    }

    @Override
    public SelectorGroup selectorGroup(int line, int column, String selectors) {
        return new StandardSelectorGroup(line, column, selectors);
    }

    @Override
    public Selector selector(int line, int column, String raw) {
        return new StandardSelector(line, column, raw);
    }
}
