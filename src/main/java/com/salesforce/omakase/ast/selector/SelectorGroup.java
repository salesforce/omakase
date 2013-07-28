/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import java.util.List;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorGroup extends Syntax {
    List<Selector> selectors();
}
