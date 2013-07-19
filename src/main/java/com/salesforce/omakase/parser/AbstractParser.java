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
}
