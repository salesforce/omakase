/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.observer.Observer;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StyleSheetParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Observer> adapters;

    /**
     * @param adapters
     *            TODO
     */
    public Omakase(Observer... adapters) {
        this.adapters = Lists.newArrayList(adapters);
    }

    /**
     * TODO Description
     * 
     * @param input
     *            TODO
     */
    public void parse(CharSequence input) {
        Stream stream = new Stream(input);
        new StyleSheetParser().parse(stream, adapters);
    }

    /**
     * TODO Description
     * 
     * @param adapters
     *            TODO
     * @return TODO
     */
    public static Omakase using(Observer... adapters) {
        return new Omakase(adapters);
    }
}
