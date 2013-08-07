/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.consumer.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Context {
    private final List<Plugin> plugins = Lists.newArrayList();

    /**
     * TODO Description
     * 
     * @param plugin
     *            TODO
     * @return TODO
     */
    public Context plugin(Plugin plugin) {
        plugins.add(checkNotNull(plugin, "plugin cannot be null"));
        return this;
    }
}
