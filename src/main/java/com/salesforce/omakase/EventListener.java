/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface EventListener {
	/**
	 * TODO Description
	 * 
	 * @param selectorGroup
	 *            TODO
	 */
	public void selectorGroup(SelectorGroup selectorGroup);

	/**
	 * TODO Description
	 * 
	 * @param declaration
	 *            TODO
	 */
	public void declaration(Declaration declaration);
}
