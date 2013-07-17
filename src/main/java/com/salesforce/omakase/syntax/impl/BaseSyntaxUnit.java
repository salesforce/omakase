/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax.impl;

import com.salesforce.omakase.syntax.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class BaseSyntaxUnit implements Syntax {
	private final int line;
	private final int column;

	/**
	 * TODO
	 * 
	 * @param line
	 *            TODO
	 * @param column
	 *            TODO
	 */
	public BaseSyntaxUnit(int line, int column) {
		this.line = line;
		this.column = column;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}
}
