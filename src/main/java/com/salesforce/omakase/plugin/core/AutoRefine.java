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

package com.salesforce.omakase.plugin.core;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.syntax.*;

import java.util.EnumSet;

/**
 * Automatically refines {@link Refinable} types.
 * <p>
 * This plugin should be added to the parse operation as the easiest to way ensure refinement of certain or all {@link Refinable}
 * types is performed. It should be added last so that other custom refiners will not be preempted.
 * <p>
 * Refinement ensures that subscription methods to leaf syntax units (such as {@link UrlFunctionValue} or {@link ClassSelector})
 * are delivered. Such units will not be delivered if the parent selector or declaration was not refined. For more information on
 * refinement see the main readme file.
 * <p>
 * Example:
 * <pre><code>
 *     Omakase.use(AutoRefine.everything()).use(...).process();
 *     Omakase.use(AutoRefine.only(Match.FUNCTIONS).use(...).process();
 * </code></pre>
 * If including this plugin as a dependency then it's preferred to refine everything, otherwise consider requiring a more
 * specific refiner plugin such as {@link DeclarationPlugin} instead.
 *
 * @author nmcwilliams
 */
public final class AutoRefine implements DependentPlugin {
    private static final Iterable<Class<? extends Plugin>> AT_RULES = ImmutableList.of(
        MediaPlugin.class, KeyframesPlugin.class, FontFacePlugin.class, SupportsPlugin.class
    );

    private static final Iterable<Class<? extends Plugin>> FUNCTIONS = ImmutableList.of(
        UrlPlugin.class, LinearGradientPlugin.class
    );

    /** Types of {@link Refinable}s to auto-refine. */
    public enum Match {
        /** Refine {@link AtRule}s */
        AT_RULES(AutoRefine.AT_RULES),

        /** Refine {@link Selector}s, {@link Declaration}s and {@link RawFunction}s. */
        RULES,

        /** Refine {@link Selector}s */
        SELECTORS(SelectorPlugin.class),

        /** Refine {@link Declaration}s */
        DECLARATIONS(DeclarationPlugin.class),

        /** Refine {@link RawFunction}s and {@link Declaration}s */
        FUNCTIONS(AutoRefine.FUNCTIONS);

        private final Iterable<Class<? extends Plugin>> plugins;

        Match() {
            this(ImmutableList.of());
        }

        Match(Class<? extends Plugin> plugin) {
            this.plugins = ImmutableList.of(plugin);
        }

        Match(Iterable<Class<? extends Plugin>> plugins) {
            this.plugins = plugins;
        }

        /**
         * Gets the plugins needed to auto-refine this type.
         *
         * @return The plugins.
         */
        public Iterable<Class<? extends Plugin>> plugins() {
            return plugins;
        }
    }

    private final EnumSet<Match> matches;

    /**
     * Creates a new {@link AutoRefine} matching all {@link Refinable}s.
     */
    public AutoRefine() {
        this.matches = EnumSet.allOf(Match.class);
    }

    /**
     * Creates a new {@link AutoRefine} matching the specified types.
     *
     * @param matches
     *     Types of {@link Refinable}s to auto-refine.
     */
    public AutoRefine(EnumSet<Match> matches) {
        if (matches.contains(Match.FUNCTIONS)) {
            matches.add(Match.DECLARATIONS);
        }
        if (matches.contains(Match.RULES)) {
            matches.add(Match.SELECTORS);
            matches.add(Match.DECLARATIONS);
            matches.add(Match.FUNCTIONS);
        }
        this.matches = matches;
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        for (Match match : matches) {
            for (Class<? extends Plugin> plugin : match.plugins()) {
                registry.require(plugin);
            }
        }
    }

    /**
     * Creates an {@link AutoRefine} that matches everything.
     *
     * @return The new {@link AutoRefine} instance.
     */
    public static AutoRefine everything() {
        return new AutoRefine(EnumSet.allOf(Match.class));
    }

    /**
     * Creates an {@link AutoRefine} that matches the specified types.
     *
     * @param match
     *     The first match.
     * @param matches
     *     Optional additional matches.
     *
     * @return The new {@link AutoRefine} instance.
     */
    public static AutoRefine only(Match match, Match... matches) {
        return new AutoRefine(EnumSet.of(match, matches));
    }
}
