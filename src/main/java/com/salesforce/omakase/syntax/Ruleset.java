/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import java.util.List;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Ruleset extends Syntax {
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
