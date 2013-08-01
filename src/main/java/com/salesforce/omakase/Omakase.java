/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.observer.Observer;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StyleSheetParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Observer> observers;

    /**
     * @param observers
     *            TODO
     */
    public Omakase(Observer... observers) {
        this.observers = ImmutableList.copyOf(observers);
    }

    /**
     * TODO Description
     * 
     * @param source
     *            TODO
     */
    public void parse(CharSequence source) {
        Stream stream = new Stream(source);
        new StyleSheetParser().parse(stream, observers);
    }

    /**
     * TODO Description
     * 
     * @param observers
     *            TODO
     * @return TODO
     */
    public static Omakase using(Observer... observers) {
        return new Omakase(observers);
    }
}
