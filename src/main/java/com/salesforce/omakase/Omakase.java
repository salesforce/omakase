/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.consumer.Plugin;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StylesheetParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Plugin> observers;

    /**
     * @param consumers
     *            TODO
     */
    public Omakase(Plugin... consumers) {
        this.observers = ImmutableList.copyOf(consumers);
    }

    /**
     * TODO Description
     * 
     * @param source
     *            TODO
     */
    public void parse(CharSequence source) {
        Stream stream = new Stream(source);
        new StylesheetParser().parse(stream, observers);
    }

    /**
     * TODO Description
     * 
     * @param consumers
     *            TODO
     * @return TODO
     */
    public static Omakase using(Plugin... consumers) {
        return new Omakase(consumers);
    }
}
