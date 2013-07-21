/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public abstract class AbstractParser implements Parser {
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }

    protected void error(Stream stream, String msg, Object... args) {
        throw new ParserException(String.format(msg, args), stream);
    }
}
