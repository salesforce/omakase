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
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.EmittingBroadcaster;
import com.salesforce.omakase.broadcaster.VisitingBroadcaster;
import com.salesforce.omakase.emitter.Emitter;
import com.salesforce.omakase.emitter.Observe;
import com.salesforce.omakase.emitter.PreProcess;
import com.salesforce.omakase.emitter.Rework;
import com.salesforce.omakase.emitter.SubscriptionPhase;
import com.salesforce.omakase.emitter.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.BroadcastingPlugin;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;

import java.util.Set;

/**
 * Handles the registry of plugins (see {@link PluginRegistry}) and also manages the broadcasting of events (see {@link
 * Broadcaster}). Note: this class is not exposed as an API itself.
 * <p/>
 * All broadcasting events are collected and stored during parsing. After the source is completely parsed, each event is replayed
 * once in each of the three phases: preprocess ({@link PreProcess} annotated methods), process ({@link Observe} and {@link
 * Rework} annotated methods), then finally validation ({@link Validate} annotated methods).
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
    public <T extends Syntax> void broadcast(T syntax) {
        broadcaster.broadcast(syntax);
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax, boolean propagate) {
        broadcaster.broadcast(syntax, propagate);
    }

    @Override
    public Broadcaster wrap(Broadcaster relay) {
        broadcaster.wrap(relay);
        return this;
    }

    /**
     * Wraps the existing broadcaster inside of the given one.
     *
     * @param broadcaster
     *     Wrap the existing broadcaster inside of this one.
     *
     * @return this, for chaining.
     */
    public Context broadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster.wrap(this.broadcaster);
        return this;
    }

    /**
     * Internal method to signify when (high-level) parsing is about to begin. This will notify all {@link Plugin}s that are
     * interested in such information, usually as a hook to add in their own dependencies on other {@link Plugin}s.
     *
     * @param em
     *     The {@link ErrorManager} instance.
     */
    protected void before(ErrorManager em) {
        // set the error manager
        emittingBroadcaster.errorManager(em);

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
        // run preprocessors
        emittingBroadcaster.phase(SubscriptionPhase.PREPROCESS);
        visitor.visit();

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
