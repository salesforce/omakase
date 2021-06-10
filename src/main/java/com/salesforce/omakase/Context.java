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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.Message.DUPLICATE_PLUGIN;
import static com.salesforce.omakase.Message.NO_SUPPLIER;
import static com.salesforce.omakase.Message.UNIQUE_PLUGIN;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.EmittingBroadcaster;
import com.salesforce.omakase.broadcast.VisitingBroadcaster;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.factory.ParserFactory;
import com.salesforce.omakase.parser.factory.StandardParserFactory;
import com.salesforce.omakase.parser.factory.StandardTokenFactory;
import com.salesforce.omakase.parser.factory.TokenFactory;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.GrammarPlugin;
import com.salesforce.omakase.plugin.ParserPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;

/**
 * Contextual state for a parsing operation.
 * <p>
 * This handles the registry of plugins (see {@link PluginRegistry}) and manages
 * the main {@link Broadcaster} instance.
 * <p>
 * All broadcasting events are collected and stored during parsing. After the
 * source is completely parsed, each event is replayed once in each of the two
 * phases: process ({@link Observe} and {@link Rework} annotated methods), then
 * validation ({@link Validate} annotated methods).
 *
 * @author nmcwilliams
 */
final class Context implements PluginRegistry {
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

    /** parser factory determines which parsers to use */
    private ParserFactory parserFactory;

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
        if (registry.containsKey(klass)) {
            throw new IllegalArgumentException(Message.fmt(DUPLICATE_PLUGIN, klass));
        }

        // handle grammar plugins
        if (plugin instanceof GrammarPlugin) {
            if (tokenFactory != null) {
                throw new IllegalStateException(Message.fmt(UNIQUE_PLUGIN, GrammarPlugin.class));
            }
            tokenFactory = checkNotNull(((GrammarPlugin)plugin).getTokenFactory(), "tokenFactory cannot be null");
        }

        // handle parser plugins
        if (plugin instanceof ParserPlugin) {
            if (parserFactory != null) {
                throw new IllegalStateException(Message.fmt(UNIQUE_PLUGIN, ParserPlugin.class));
            }
            parserFactory = checkNotNull(((ParserPlugin)plugin).getParserFactory(), "parserFactory cannot be null");
        }

        // handle plugin dependencies
        if (plugin instanceof DependentPlugin) {
            ((DependentPlugin)plugin).dependencies(this);
        }

        // add the plugin to the registry
        registry.put(klass, plugin);

        // hook up the plugin for subscription events
        emittingBroadcaster.register(plugin);
    }

    @Override
    public <T extends Plugin> T require(Class<T> klass) {
        Optional<Supplier<T>> supplier = Suppliers.get(klass);
        if (!supplier.isPresent()) throw new IllegalArgumentException(Message.fmt(NO_SUPPLIER, klass));
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
        return Optional.ofNullable(registry.getInstance(klass));
    }

    /**
     * Wraps the existing broadcaster inside of the given one.
     *
     * @param broadcaster
     *     Wrap the existing broadcaster inside of this one.
     */
    public void broadcaster(Broadcaster broadcaster) {
        checkNotNull(broadcaster, "broadcaster cannot be null");
        broadcaster.chain(this.broadcaster);
        this.broadcaster = broadcaster;
    }

    /**
     * Gets the top level {@link Broadcaster}.
     *
     * @return The broadcaster.
     */
    public Broadcaster broadcaster() {
        return broadcaster;
    }

    /**
     * Internal method to signify when (high-level) parsing is about to begin.
     */
    protected Grammar beforeParsing(ErrorManager em) {
        checkNotNull(em, "An error manager must be given to the context");

        Grammar grammar = new Grammar(
            tokenFactory != null ? tokenFactory : StandardTokenFactory.instance(),
            parserFactory != null ? parserFactory : StandardParserFactory.instance());

        emittingBroadcaster.root(broadcaster);
        emittingBroadcaster.grammar(grammar);
        emittingBroadcaster.errorManager(em);
        emittingBroadcaster.phase(SubscriptionPhase.REFINE);

        return grammar;
    }

    /**
     * Internal method to signify when (high-level) parsing is completed.
     */
    protected void afterParsing() {
        // replay broadcasts for observers and reworkers
        emittingBroadcaster.phase(SubscriptionPhase.PROCESS);
        visitor.visit(broadcaster, Status.PARSED);

        // replay broadcasts for validators
        emittingBroadcaster.phase(SubscriptionPhase.VALIDATE);
        visitor.visit(broadcaster, Status.PROCESSED);

        // notify post processors
        for (PostProcessingPlugin plugin : filter(PostProcessingPlugin.class)) {
            plugin.postProcess(this);
        }
    }

    /** helper method to get only plugins of a certain type */
    private <T extends Plugin> Iterable<T> filter(Class<T> klass) {
        return registry.values().stream().filter(klass::isInstance).map(klass::cast).collect(Collectors.toList());
    }
}
