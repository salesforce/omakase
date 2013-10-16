/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Sets;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.EmittingBroadcaster;
import com.salesforce.omakase.broadcast.VisitingBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.broadcast.emitter.Emitter;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import com.salesforce.omakase.plugin.BroadcastingPlugin;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.List;
import java.util.Set;

/**
 * Handles the registry of plugins (see {@link PluginRegistry}) and also manages the broadcasting of events (see {@link
 * Broadcaster}). Note: this class is not exposed as an API itself.
 * <p/>
 * All broadcasting events are collected and stored during parsing. After the source is completely parsed, each event is replayed
 * once in each of the two phases: process ({@link Observe} and {@link Rework} annotated methods), then validation ({@link
 * Validate} annotated methods).
 *
 * @author nmcwilliams
 */
final class Context implements Broadcaster, PluginRegistry {
    /** registry of all plugins */
    private final ClassToInstanceMap<Plugin> registry = MutableClassToInstanceMap.create();

    /** uses an {@link Emitter} to broadcast events */
    private final EmittingBroadcaster emittingBroadcaster = new EmittingBroadcaster();

    /** used to replay each broadcasted unit once per phase */
    private final VisitingBroadcaster visitor = new VisitingBroadcaster(emittingBroadcaster);

    /** main broadcaster - consumer changeable via {@link #broadcaster(Broadcaster)} */
    private Broadcaster broadcaster = visitor;

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
        if (registry.containsKey(klass)) throw new IllegalArgumentException(Message.DUPLICATE_PLUGIN.message(klass));

        // add the plugin to the registry
        registry.put(klass, plugin);

        // hook up the plugin for events
        emittingBroadcaster.register(plugin);
    }

    @Override
    public <T extends Plugin> T require(Class<T> klass) {
        Optional<Supplier<T>> supplier = Suppliers.get(klass);
        if (!supplier.isPresent()) throw new IllegalArgumentException(Message.NO_SUPPLIER.message(klass));
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
        return Optional.fromNullable(registry.getInstance(klass));
    }

    @Override
    public void broadcast(Broadcastable broadcastable) {
        broadcaster.broadcast(broadcastable);
    }

    @Override
    public void broadcast(Broadcastable broadcastable, boolean propagate) {
        broadcaster.broadcast(broadcastable, propagate);
    }

    @Override
    public void wrap(Broadcaster relay) {
        broadcaster.wrap(relay);
    }

    /**
     * Wraps the existing broadcaster inside of the given one.
     *
     * @param broadcaster
     *     Wrap the existing broadcaster inside of this one.
     */
    public void broadcaster(Broadcaster broadcaster) {
        broadcaster.wrap(this.broadcaster);
        this.broadcaster = broadcaster;
    }

    /**
     * Creates a new refiner instance with the {@link Broadcaster} currently set on this {@link Context} and the list of {@link
     * RefinerStrategy}s from all registered {@link SyntaxPlugin}s.
     * <p/>
     * This should be called <em>after</em> any calls to {@link #broadcaster (Broadcaster)}.
     *
     * @return The {@link Refiner} instance.
     */
    public Refiner createRefiner() {
        List<RefinerStrategy> customRefiners = Lists.newArrayList();

        for (SyntaxPlugin plugin : filter(SyntaxPlugin.class)) {
            customRefiners.add(plugin.getRefinerStrategy());
        }

        return new Refiner(broadcaster, customRefiners);
    }

    /**
     * Registers the {@link ErrorManager}. This should be called ahead of {@link #before()} or bad stuff will happen.
     *
     * @param em
     *     The {@link ErrorManager}.
     *
     * @return this, for chaining.
     */
    public Context errorManager(ErrorManager em) {
        emittingBroadcaster.errorManager(em);
        return this;
    }

    /**
     * Internal method to signify when (high-level) parsing is about to begin. This will notify all {@link Plugin}s that are
     * interested in such information, usually as a hook to add in their own dependencies on other {@link Plugin}s.
     */
    protected void before() {
        // let plugins register their dependencies. dependencies can result in their own new dependencies, so this gets
        // a little hairy... guava to the rescue.
        final Set<DependentPlugin> processed = Sets.newHashSet();
        Set<DependentPlugin> unprocessed = ImmutableSet.copyOf(filter(DependentPlugin.class));
        Set<DependentPlugin> updated;

        while (!unprocessed.isEmpty()) {
            for (DependentPlugin plugin : unprocessed) {
                plugin.dependencies(this);
            }
            processed.addAll(unprocessed);
            updated = ImmutableSet.copyOf(filter(DependentPlugin.class));
            unprocessed = Sets.difference(updated, processed);
        }

        // distribute the broadcaster to plugins that need it
        for (BroadcastingPlugin plugin : filter(BroadcastingPlugin.class)) {
            plugin.broadcaster(this);
        }
    }

    /**
     * Internal method to signify when (high-level) parsing is completed. This will notify all {@link Plugin}s that are interested
     * in such information. This also replays the stored broadcasts for each phase.
     */
    protected void after() {
        // run observers and reworkers
        emittingBroadcaster.phase(SubscriptionPhase.PROCESS);
        visitor.visit();

        // run validators
        emittingBroadcaster.phase(SubscriptionPhase.VALIDATE);
        visitor.visit();

        // notify post processors
        for (PostProcessingPlugin plugin : filter(PostProcessingPlugin.class)) {
            plugin.postProcess(this);
        }
    }

    /** helper method to get only plugins of a certain type */
    private <T extends Plugin> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(registry.values(), klass);
    }
}
