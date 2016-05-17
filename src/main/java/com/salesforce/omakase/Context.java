/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.MutableClassToInstanceMap;
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
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.StandardTokenFactory;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.plugin.BroadcastingPlugin;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles the registry of plugins (see {@link PluginRegistry}) and also manages the broadcasting of events (see {@link
 * Broadcaster}). Note: this class is not exposed as an API itself.
 * <p>
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

    /** token factory affects delimiter grammar rules */
    private TokenFactory tokenFactory;

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

        // handle plugin dependencies
        if (plugin instanceof DependentPlugin) {
            ((DependentPlugin)plugin).dependencies(this);
        }

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
    public <T extends TokenFactory> T requireTokenFactory(Class<T> klass, Supplier<T> supplier) {
        if (klass.isInstance(tokenFactory)) {
            @SuppressWarnings("unchecked")
            T instance = (T)tokenFactory;
            return instance;
        } else if (tokenFactory != null) {
            throw new IllegalArgumentException(Message.ONLY_ONE_TOKEN_FACTORY.message(tokenFactory.getClass()));
        }

        T supplied = supplier.get();
        this.tokenFactory = checkNotNull(supplied, "cannot assign a null token factory");
        return supplied;
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
     * Creates a new {@link MasterRefiner} instance with the {@link Broadcaster} currently set on this {@link Context} and with
     * the {@link Refiner}s from all registered {@link SyntaxPlugin}s. This will use the {@link StandardTokenFactory}.
     * <p>
     * This should be called <em>after</em> any calls to {@link #broadcaster (Broadcaster)}.
     *
     * @return The {@link MasterRefiner} instance.
     */
    public MasterRefiner createRefiner() {
        if (tokenFactory == null) {
            tokenFactory = StandardTokenFactory.instance();
        }

        MasterRefiner refiner = new MasterRefiner(broadcaster, tokenFactory);

        for (SyntaxPlugin plugin : filter(SyntaxPlugin.class)) {
            plugin.registerRefiners(refiner);
        }

        return refiner;
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
