/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.broadcaster.Broadcaster;

/**
 * TESTME Combines two {@link Parser}s together. If the first parser does not succeed (i.e., returns false) then the
 * second parse will be tried.
 * 
 * @author nmcwilliams
 */
public class CombinationParser extends AbstractParser {
    private final Parser first;
    private final Parser second;

    /**
     * Construct a new {@link CombinationParser} instance with the given two {@link Parser}s.
     * 
     * @param first
     *            The first {@link Parser} to try.
     * @param second
     *            The second {@link Parser} to try,
     */
    public CombinationParser(Parser first, Parser second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        return first.parse(stream, broadcaster) ? true : second.parse(stream, broadcaster);
    }
}
