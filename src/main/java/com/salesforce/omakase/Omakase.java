/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.adapter.Adapter;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StyleSheetParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Adapter> adapters;

    /**
     * @param adapters
     *            TODO
     */
    public Omakase(Adapter... adapters) {
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
        new StyleSheetParser().parseRaw(stream, adapters);
    }

    /**
     * TODO Description
     * 
     * @param adapters
     *            TODO
     * @return TODO
     */
    public static Omakase using(Adapter... adapters) {
        return new Omakase(adapters);
    }
}
