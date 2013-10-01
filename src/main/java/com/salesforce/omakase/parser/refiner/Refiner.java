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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Responsible for refining {@link Refinable} objects, such as {@link Selector} and {@link Declaration}.
 * <p/>
 * A list of {@link RefinableStrategy} objects can be registered to customize and extends the standard CSS syntax. See the readme
 * file for more details.
 *
 * @author nmcwilliams
 * @see RefinableStrategy
 * @see SyntaxPlugin
 */
public final class Refiner {
    private static final RefinableStrategy STANDARD = new StandardRefinableStrategy();

    private final Broadcaster broadcaster;
    private final ImmutableList<RefinableStrategy> strategies;

    /**
     * Creates a new {@link Refiner} instance with the given {@link Broadcaster} to use for new refined AST objects.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     */
    public Refiner(Broadcaster broadcaster) {
        this(broadcaster, ImmutableList.<RefinableStrategy>of());
    }

    /**
     * Creates a new {@link Refiner} instance with the given {@link Broadcaster} and list of custom {@link RefinableStrategy}
     * objects.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     * @param strategies
     *     List of {@link RefinableStrategy} objects to consult in the refining process, in order.
     */
    public Refiner(Broadcaster broadcaster, Iterable<RefinableStrategy> strategies) {
        this.broadcaster = broadcaster;
        this.strategies = ImmutableList.copyOf(strategies);
    }

    /**
     * Refines an {@link AtRule} object.
     * <p/>
     * Any registered {@link RefinableStrategy} objects will be consulted in the registered order. If no {@link RefinableStrategy}
     * decides to handle the instance, or if none are registered then {@link RefinableStrategy#refineAtRule(AtRule, Broadcaster,
     * Refiner)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link AtRule#refine()} instead.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     */
    public void refine(AtRule atRule) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineAtRule(atRule, broadcaster, this)) return;
        }

        // fallback to the default refiner
        STANDARD.refineAtRule(atRule, broadcaster, this);
    }

    /**
     * Refines a {@link Selector} object.
     * <p/>
     * Any registered {@link RefinableStrategy} objects will be consulted in the registered order. If no {@link RefinableStrategy}
     * decides to handle the instance, or if none are registered then {@link RefinableStrategy#refineSelector(Selector,
     * Broadcaster, Refiner)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Selector#refine()} instead.
     *
     * @param selector
     *     The {@link Selector} to refine.
     */
    public void refine(Selector selector) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineSelector(selector, broadcaster, this)) return;
        }

        // fallback to the default refiner
        STANDARD.refineSelector(selector, broadcaster, this);
    }

    /**
     * Refines a {@link Declaration} object.
     * <p/>
     * Any registered {@link RefinableStrategy} objects will be consulted in the registered order. If no {@link RefinableStrategy}
     * decides to handle the instance, or if none are registered then {@link RefinableStrategy#refineDeclaration (Declaration,
     * Broadcaster)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Declaration#refine()} instead.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     */
    public void refine(Declaration declaration) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineDeclaration(declaration, broadcaster, this)) return;
        }

        // fallback to the default refiner
        STANDARD.refineDeclaration(declaration, broadcaster, this);
    }

    /**
     * Gets the {@link Broadcaster} registered with this {@link Refiner}.
     *
     * @return The {@link Broadcaster}.
     */
    public Broadcaster broadcaster() {
        return broadcaster;
    }
}

