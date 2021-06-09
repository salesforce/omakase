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

import java.util.Optional;
import java.util.function.Supplier;

import com.salesforce.omakase.plugin.Plugin;

/**
 * Registry of {@link Plugin}s.
 * <p>
 * This allows you to require plugins as dependencies and also retrieve registered plugin instances. Note that only one instance
 * of a specific plugin can be registered.
 *
 * @author nmcwilliams
 */
public interface PluginRegistry {
    /**
     * Registers {@link Plugin} instances to this {@link PluginRegistry}.
     * <p>
     * Only <b>one</b> instance of a specific {@link Plugin} can be registered to a single {@link PluginRegistry}. This is to
     * make {@link #require(Class)} and {@link #retrieve(Class)} work in a simple way. {@link Plugin}s should be coded with
     * this in mind.
     *
     * @param plugins
     *     The {@link Plugin}(s) to register.
     */
    void register(Iterable<? extends Plugin> plugins);

    /**
     * Registers a single {@link Plugin}.
     * <p>
     * Only <b>one</b> instance of a specific {@link Plugin} can be registered to a single {@link PluginRegistry}. This is to
     * make {@link #require(Class)} and {@link #retrieve(Class)} work in a simple way. {@link Plugin}s should be coded with
     * this in mind.
     *
     * @param plugin
     *     The plugin to register.
     */
    void register(Plugin plugin);

    /**
     * Specifies that a particular plugin is required as a dependency.
     * <p>
     * If the plugin is already registered then the registered instance will simply be returned. If the plugin is not registered
     * then a new instance will be created, registered, then returned.
     * <p>
     * This method is only for library-provided plugins. To require a custom plugin, use {@link #require(Class, Supplier)}
     * instead.
     * <p>
     * This method is usually used within the {@link DependentPlugin#dependencies(PluginRegistry)} method.
     * <p>
     * Examples:
     * <pre>
     * {@code registry.require(SyntaxTree.class)}
     * {@code registry.require(UrlPlugin.class)}
     * </pre>
     *
     * @param <T>
     *     Type of the plugin.
     * @param klass
     *     The plugin class.
     *
     * @return An instance of the plugin.
     */
    <T extends Plugin> T require(Class<T> klass);

    /**
     * Same as {@link #require(Class)}, except this should be used for custom (non-library-provided) plugins.
     * <p>
     * The {@link Supplier} is used to get an instance if one is not already registered. This method is usually used within the
     * {@link DependentPlugin#dependencies(PluginRegistry)} method.
     *
     * @param <T>
     *     Type of the plugin.
     * @param klass
     *     The plugin class.
     * @param supplier
     *     Supplies an instance of the plugin.
     *
     * @return An instance of the plugin.
     */
    <T extends Plugin> T require(Class<T> klass, Supplier<T> supplier);

    /**
     * Retrieves the instance of the given {@link Plugin} type. This is normally used by {@link Plugin}s to access another {@link
     * Plugin} instance that they are dependent on.
     *
     * @param <T>
     *     Get the instance of this {@link Plugin} type.
     * @param klass
     *     Class of the plugin to retrieve.
     *
     * @return The instance, or an empty {@link Optional} if no instance of the {@link Plugin} was registered.
     */
    <T extends Plugin> Optional<T> retrieve(Class<T> klass);
}
