/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import com.salesforce.omakase.syntax.impl.RefinedDeclaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Declaration extends Syntax, Refinable<RefinedDeclaration> {
	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	String property();

	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	String value();
}
