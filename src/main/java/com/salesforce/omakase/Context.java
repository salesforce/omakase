/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.MutableClassToInstanceMap;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.EmittingBroadcaster;
import com.salesforce.omakase.broadcaster.VisitingBroadcaster;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.SubscriptionPhase;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.*;

/**
 * Handles the registry of plugins (see {@link PluginRegistry}) and also manages the broadcasting events (see
 * {@link Broadcaster}). Note: this class is not exposed as an API itself.
 * 
 * @author nmcwilliams
 */
final class Context implements Broadcaster, PluginRegistry {
    /** registry of all plugins */
    private final ClassToInstanceMap<Plugin> registry = MutableClassToInstanceMap.create();

    /** uses an {@link Emitter} to broadcast events */
    private final EmittingBroadcaster emittingBroadcaster = new EmittingBroadcaster();

    /** main broadcaster */
    private final VisitingBroadcaster broadcaster = new VisitingBroadcaster(emittingBroadcaster);

    /** internal construction only */
    Context() {}

    @Override
    public void register(Iterable<? extends Plugin> plugins) {
        for (Plugin plugin : plugins) {
            register(plugin);
        }
    }

    @Override
    public void register(Plugin plugin) {
        // get the class of the plugin, which we will use to index this instance in the registry
        Class<? extends Plugin> klass = plugin.getClass();

        // only one instance allowed per plugin type
        checkArgument(!registry.containsKey(klass), Message.DUPLICATE_PLUGIN.message(klass));

        // add the plugin to the registry
        registry.put(klass, plugin);

        // hook up the plugin for events
        emittingBroadcaster.register(plugin);
    }

    @Override
    public <T extends Plugin> T require(Class<T> klass) {
        Optional<Supplier<T>> supplier = Suppliers.get(klass);
        checkArgument(supplier.isPresent(), Message.NO_SUPPLIER.message(klass));
        return require(klass, supplier.get());
    }

    @Override
    public <T extends Plugin> T require(Class<T> klass, Supplier<T> supplier) {
        T instance = registry.getInstance(klass);

        if (instance == null) {
            instance = supplier.get();
            register(instance);
        }

        return instance;
    }

    @Override
    public <T extends Plugin> Optional<T> retrieve(Class<T> klass) {
        return Optional.of(registry.getInstance(klass));
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        broadcaster.broadcast(syntax);
    }

    /** helper method to get only plugins of a certain type */
    private <T extends Plugin> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(registry.values(), klass);
    }

    /**
     * Internal method to signify when (high-level) parsing is about to begin. This will notify all {@link Plugin}s that
     * are interested in such information, usually as a hook to add in their own dependencies on other {@link Plugin}s.
     * 
     * @param em
     *            The {@link ErrorManager} instance.
     */
    protected void before(ErrorManager em) {
        // set the error manager
        emittingBroadcaster.errorManager(em);

        // distribute the broadcaster to plugins that need it
        for (BroadcastingPlugin plugin : filter(BroadcastingPlugin.class)) {
            plugin.broadcaster(this);
        }

        // let plugins register their dependencies
        for (DependentPlugin plugin : filter(DependentPlugin.class)) {
            plugin.dependencies(this);
        }
    }

    /**
     * Internal method to signify when (high-level) parsing is completed. This will notify all {@link Plugin}s that are
     * interested in such information. This also replays the stored broadcasts for each phase.
     */
    protected void after() {
        // notify preprocessing plugins
        for (PreProcessingPlugin plugin : filter(PreProcessingPlugin.class)) {
            plugin.beforePreProcess();
        }

        // run preprocessors
        emittingBroadcaster.phase(SubscriptionPhase.PREPROCESS);
        broadcaster.visit();

        // notify preprocessing plugins
        for (PreProcessingPlugin plugin : filter(PreProcessingPlugin.class)) {
            plugin.afterPreProcess();
        }

        // run observers and reworkers
        emittingBroadcaster.phase(SubscriptionPhase.PROCESS);
        broadcaster.visit();

        // run validators
        emittingBroadcaster.phase(SubscriptionPhase.VALIDATE);
        broadcaster.visit();

        // notify post processors
        for (PostProcessingPlugin plugin : filter(PostProcessingPlugin.class)) {
            plugin.postProcess(this);
        }
    }
}
