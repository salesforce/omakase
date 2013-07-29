/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Builder {
    Stylesheet stylesheet();

    Rule rule();

    Declaration declaration();

    SelectorGroup selectorGroup();

}
