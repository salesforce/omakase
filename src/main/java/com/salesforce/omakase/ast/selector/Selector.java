/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import java.util.List;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.syntax.impl.RefinedSelector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Selector extends Syntax, Refinable<RefinedSelector> {
    List<SelectorPart> parts();
}
