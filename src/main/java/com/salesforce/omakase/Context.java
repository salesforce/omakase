/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MutableClassToInstanceMap;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Context {
    private final ClassToInstanceMap<Plugin> plugins = MutableClassToInstanceMap.create();

    /**
     * TODO
     * 
     * @param plugins
     *            TODO
     */
    public Context(Plugin... plugins) {
        this(Lists.newArrayList(plugins));
    }

    /**
     * TODO
     * 
     * @param plugins
     *            TODO
     */
    public Context(Iterable<Plugin> plugins) {
        for (Plugin plugin : plugins) {
            plugin(plugin);
        }
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param type
     *            TODO
     * @param supplier
     *            TODO
     * @return TODO
     */
    public <T extends Plugin> T require(Class<T> type, Supplier<T> supplier) {
        return Optional.fromNullable(plugins.getInstance(type)).or(supplier);
    }

    /**
     * TODO Description
     * 
     * @param plugin
     *            TODO
     * @return TODO
     */
    public Context plugin(Plugin plugin) {
        checkNotNull(plugin, "plugin cannot be null");

        plugins.put(plugin.getClass(), plugin);
        return this;
    }
}
