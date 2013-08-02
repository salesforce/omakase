/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.consumer.Consumer;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StylesheetParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Consumer> observers;

    /**
     * @param workers
     *            TODO
     */
    public Omakase(Consumer... workers) {
        this.observers = ImmutableList.copyOf(workers);
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
     * @param observers
     *            TODO
     * @return TODO
     */
    public static Omakase using(Consumer... observers) {
        return new Omakase(observers);
    }
}
