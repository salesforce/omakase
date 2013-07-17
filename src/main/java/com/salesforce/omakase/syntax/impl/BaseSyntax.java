/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.salesforce.omakase.syntax.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 */
public abstract class BaseSyntax<T> implements Syntax<T> {
	private final int line;
	private final int col;

	public BaseSyntax(int line, int col) {
		this.line = line;
		this.col = col;
	}
}
