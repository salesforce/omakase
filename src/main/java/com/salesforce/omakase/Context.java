/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MutableClassToInstanceMap;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Context implements Broadcaster {
    private static final String NO_SUPPLER = "No supplier defined for %s. Use require(Class, Supplier) instead.";
    private static final String DUPLICATE = "Only one plugin instance of each type allowed: %s";

    /** registry of all plugins */
    private final ClassToInstanceMap<Plugin> registry = MutableClassToInstanceMap.create();

    /** list of all plugins that have dependencies (e.g., needs access to this {@link Context} */
    private final List<DependentPlugin> dependentPlugins = Lists.newArrayList();

    /** used for propagating new syntax unit created or change events */
    private final Emitter emitter = new Emitter();

    /** internal construction only */
    Context() {}

    /**
     * Registers {@link Plugin} instances to this {@link Context}.
     * 
     * <p>
     * Only <b>one</b> instance of a {@link Plugin} can be registered to a single {@link Context}. This is to make
     * {@link #require(Class)} and {@link #retrieve(Class)} work in a simple way. {@link Plugin}s should be coded with
     * this in mind.
     * 
     * @param plugins
     *            The {@link Plugin}(s) to register.
     */
    public void plugins(Plugin... plugins) {
        for (Plugin plugin : plugins) {
            // get the class of the plugin, which we will use to index this instance in the registry
            Class<? extends Plugin> klass = plugin.getClass();

            // only one instance allowed per plugin type
            checkArgument(!registry.containsKey(klass), String.format(DUPLICATE, klass));

            // add the plugin to the registry
            registry.put(klass, plugin);

            // hook up the plugin for events
            emitter.register(plugin);

            // check if the plugin has dependencies on other plugins
            if (plugin instanceof DependentPlugin) {
                dependentPlugins.add((DependentPlugin)plugin);
            }
        }
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
    public <T extends Plugin> T require(Class<T> klass) {
        Optional<Supplier<T>> supplier = Suppliers.get(klass);
        checkArgument(supplier.isPresent(), String.format(NO_SUPPLER, klass));
        return require(klass, supplier.get());
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @param supplier
     *            TODO
     * @return TODO
     */
    public <T extends Plugin> T require(Class<T> klass, Supplier<T> supplier) {
        T instance = registry.getInstance(klass);

        if (instance == null) {
            instance = supplier.get();
            plugins(instance);
        }

        return instance;
    }

    /**
     * Retrieves the instance of the given {@link Plugin} type. This is normally used by {@link Plugin}s to access
     * another {@link Plugin} instance that they are dependent on.
     * 
     * @param <T>
     *            Get the instance of this {@link Plugin} type.
     * @param klass
     *            Class of the plugin to retrieve.
     * @return The instance, or {@link Optional#absent()} if no instance of the {@link Plugin} was registered.
     */
    public <T extends Plugin> Optional<T> retrieve(Class<T> klass) {
        return Optional.of(registry.getInstance(klass));
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        emitter.emit(type, syntax);
    }

    /**
     * Internal method to signify when (high-level) parsing is about to begin. This will notify all {@link Plugin}s that
     * are interested in such information, usually as a hook to add in their own dependencies on other {@link Plugin}s
     * using {@link #require(Class)}.
     */
    protected void before() {
        for (DependentPlugin plugin : dependentPlugins) {
            plugin.before(this);
        }
    }

    /**
     * Internal method to signify when (high-level) parsing is completed. This will notify all {@link Plugin}s that are
     * interested in such information, usually in cases where the {@link Plugin} needs to wait until all selectors
     * and/or declarations in the source have been parsed.
     */
    protected void after() {
        for (DependentPlugin plugin : dependentPlugins) {
            plugin.after(this);
        }
    }
}
