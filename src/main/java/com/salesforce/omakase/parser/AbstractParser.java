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
    /**
     * TODO Description
     * 
     * @param other
     *            TODO
     * @return TODO
     */
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }
}
