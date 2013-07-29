/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SyntaxFactory {
    Stylesheet stylesheet(int line, int column);

    Rule rule(int line, int column);

    Declaration declaration(int line, int column, String property, String value);

    SelectorGroup selectorGroup(int line, int column, String selectors);

    Selector selector(int line, int column, String raw);
}
