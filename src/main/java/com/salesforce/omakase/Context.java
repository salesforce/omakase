/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.plugin.Filter;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Context {
    private final ClassToInstanceMap<Plugin> plugins = MutableClassToInstanceMap.create();
    private final Emitter emitter = new Emitter();

    /** for internal construction */
    Context() {}

    /**
     * TODO Description This is to make #require and #retrieve work in a simple way
     * 
     * @param pluginsToRegister
     *            TODO
     */
    public void plugins(Plugin... pluginsToRegister) {
        for (Plugin plugin : pluginsToRegister) {
            Class<? extends Plugin> klass = plugin.getClass();

            // only one instance per plugin allowed per type.
            checkArgument(!plugins.containsKey(klass), String.format("Only one plugin instance of each type allowed: %s", klass));

            // add the plugin to the list
            plugins.put(klass, plugin);

            // register the plugin for events
            emitter.register(plugin);
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
     * @param klass
     *            TODO
     * @return TODO
     */
    public Filter require(Class<Filter> klass) {
        return require(klass, Suppliers.FILTER);
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @return TODO
     */
    public <T extends Plugin> Optional<T> retrieve(Class<T> klass) {
        return Optional.of(plugins.getInstance(klass));
    }

    /**
     * TODO Description
     * 
     * @param syntax
     *            TODO
     * @return TODO
     */
    public <T extends Syntax> Context broadcast(T syntax) {
        emitter.emit(syntax);
        return this;
    }
}
