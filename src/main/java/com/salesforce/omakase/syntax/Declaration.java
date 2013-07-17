package com.salesforce.omakase.syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 */
public interface Declaration extends Syntax<RefinedDeclaration> {
	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	String getProperty();

	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	String getValue();
}
