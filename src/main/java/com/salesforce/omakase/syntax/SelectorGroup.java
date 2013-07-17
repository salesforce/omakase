/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import com.salesforce.omakase.syntax.impl.RefinedSelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorGroup extends Syntax, Refinable<RefinedSelectorGroup> {
	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	String selectorGroup();
}
