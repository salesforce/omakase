package com.salesforce.omakase.syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 * @param <T>
 *            TODO
 */
public interface Syntax<T> {
	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	int getLine();

	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	int getColumn();

	/**
	 * TODO Description
	 * 
	 * @return TODO
	 */
	T refine();
}
