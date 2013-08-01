/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.List;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Rule extends Statement {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    SelectorGroup selectorGroup();

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<Declaration> declarations();
}
