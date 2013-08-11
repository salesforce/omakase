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

    private final List<DependentPlugin> dependentPlugins = Lists.newArrayList();

    /** used for propagating new syntax creation or change events */
    private final Emitter emitter = new Emitter();

    /** internal construction only */
    Context() {}

    /**
     * TODO Description
     * 
     * <p>
     * This is to make #require and #retrieve work in a simple way
     * 
     * @param plugins
     *            TODO
     */
    public void plugins(Plugin... plugins) {
        for (Plugin plugin : plugins) {
            // get the class of the plugin, which we will use to index this instance in the registry
            Class<? extends Plugin> klass = plugin.getClass();

            checkArgument(!registry.containsKey(klass), String.format(DUPLICATE, klass));

            // add the plugin to the registry
            registry.put(klass, plugin);

            // hook up the plugin for events
            emitter.register(plugin);

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
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @return TODO
     */
    public <T extends Plugin> Optional<T> retrieve(Class<T> klass) {
        return Optional.of(registry.getInstance(klass));
    }

    @Override
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax) {
        emitter.emit(type, syntax);
    }

    /**
     * TODO Description
     * 
     */
    protected void before() {
        for (DependentPlugin plugin : dependentPlugins) {
            plugin.before(this);
        }
    }

    /**
     * TODO Description
     * 
     */
    protected void after() {
        for (DependentPlugin plugin : dependentPlugins) {
            plugin.after(this);
        }
    }
}
