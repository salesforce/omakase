/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.StylesheetParser;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Omakase {
    private final List<Plugin> plugins;

    /**
     * @param consumers
     *            TODO
     */
    private Omakase(Plugin... consumers) {
        this.plugins = Lists.newArrayList(consumers);
    }

    /**
     * TODO Description
     * 
     * @param plugin
     *            TODO
     * @return TODO
     */
    public Omakase request(Plugin plugin) {
        plugins.add(plugin);
        return this;
    }

    /**
     * TODO Description
     * 
     * @param source
     *            TODO
     * @return TODO
     */
    public Context process(CharSequence source) {
        Stream stream = new Stream(source);
        Context context = new Context(plugins);

        new StylesheetParser().parse(stream, context);
        return context;
    }

    /**
     * TODO Description
     * 
     * @param plugins
     *            TODO
     * @return TODO
     */
    public static Omakase request(Plugin... plugins) {
        return new Omakase(plugins);
    }
}
