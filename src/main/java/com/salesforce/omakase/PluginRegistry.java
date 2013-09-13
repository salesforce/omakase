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
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Registry of {@link Plugin}s.
 * <p/>
 * This allows you to require plugins as dependencies and also retrieve registered plugin instances. Note that only one instance
 * of a plugin can be registered.
 *
 * @author nmcwilliams
 */
public interface PluginRegistry {
    /**
     * Registers {@link Plugin} instances to this {@link PluginRegistry}.
     * <p/>
     * Only <b>one</b> instance of a {@link Plugin} can be registered to a single {@link PluginRegistry}. This is to make {@link
     * #require(Class)} and {@link #retrieve(Class)} work in a simple way. {@link Plugin}s should be coded with this in mind.
     *
     * @param plugins
     *     The {@link Plugin}(s) to register.
     */
    void register(Iterable<? extends Plugin> plugins);

    /**
     * Registers a single {@link Plugin}.
     * <p/>
     * Only <b>one</b> instance of a {@link Plugin} can be registered to a single {@link PluginRegistry}. This is to make {@link
     * #require(Class)} and {@link #retrieve(Class)} work in a simple way. {@link Plugin}s should be coded with this in mind.
     *
     * @param plugin
     *     The plugin to register.
     */
    void register(Plugin plugin);

    /**
     * Specifies that a particular plugin is required as a dependency. If the plugin is already registered then the registered
     * instance will simply be returned. If the plugin is not registered then a new instance will be created, registered, then
     * returned.
     * <p/>
     * This method is only for library-provided plugins. To require a custom plugin, use {@link #require(Class, Supplier)}
     * instead.
     * <p/>
     * This method is usually used within the {@link DependentPlugin#dependencies(PluginRegistry)} method.
     * <p/>
     * Examples:
     * <pre>
     * {@code registry.require(SyntaxTree.class)}
     * {@code registry.require(AutoRefiner.class).selectors();}
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
     * Same as {@link #require(Class)}, except this should be used for custom (non-library-provided) plugins. The {@link Supplier}
     * is used to get an instance if one is not already registered.
     * <p/>
     * This method is usually used within the {@link DependentPlugin#dependencies(PluginRegistry)} method.
     *
     * @param <T>
     *     Type of the plugin.
     * @param klass
     *     The plugin class.
     * @param supplier
     *     Supplies an instance of the plugin. It's fine to use an anonymous class here.
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
     * @return The instance, or {@link Optional#absent()} if no instance of the {@link Plugin} was registered.
     */
    <T extends Plugin> Optional<T> retrieve(Class<T> klass);
}
