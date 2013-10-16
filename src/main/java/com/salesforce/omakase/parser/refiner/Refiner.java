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
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.List;

/**
 * Used for refining {@link Refinable} objects, such as {@link Selector} and {@link Declaration}, and other "refinable" syntax
 * such as custom functions.
 * <p/>
 * {@link RefinerStrategy} objects can be registered to customize and extends the standard CSS syntax. See the readme file for
 * more details.
 *
 * @author nmcwilliams
 * @see SyntaxPlugin
 */
public final class Refiner {
    private static final StandardRefinerStrategy STANDARD = new StandardRefinerStrategy();

    private final Broadcaster defaultBroadcaster;
    private final List<AtRuleRefinerStrategy> atRuleRefiners;
    private final List<SelectorRefinerStrategy> selectorRefiners;
    private final List<DeclarationRefinerStrategy> declarationRefiners;
    private final List<FunctionRefinerStrategy> functionRefiners;

    /**
     * Creates a new {@link Refiner} instance with the given {@link Broadcaster} to use for new refined AST objects.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     */
    public Refiner(Broadcaster broadcaster) {
        this.defaultBroadcaster = broadcaster;
        this.atRuleRefiners = ImmutableList.of();
        this.selectorRefiners = ImmutableList.of();
        this.declarationRefiners = ImmutableList.of();
        this.functionRefiners = ImmutableList.of();
    }

    /**
     * Creates a new {@link Refiner} instance with the given {@link Broadcaster} and list of custom {@link RefinerStrategy}
     * objects.
     * <p/>
     * For performance reasons, this should be called as few times as possible! If creating a refiner in this way then cache and
     * reuse it if possible.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     * @param strategies
     *     The custom {@link RefinerStrategy}s, in explicit order.
     */
    public Refiner(Broadcaster broadcaster, Iterable<RefinerStrategy> strategies) {
        this.defaultBroadcaster = broadcaster;

        // ladies and gentlemen, consumer convenience over efficiency and performance
        ImmutableList.Builder<AtRuleRefinerStrategy> atRuleBuilder = ImmutableList.builder();
        ImmutableList.Builder<SelectorRefinerStrategy> selectorBuilder = ImmutableList.builder();
        ImmutableList.Builder<DeclarationRefinerStrategy> declarationBuilder = ImmutableList.builder();
        ImmutableList.Builder<FunctionRefinerStrategy> functionValueBuilder = ImmutableList.builder();

        for (RefinerStrategy strategy : strategies) {
            if (strategy instanceof AtRuleRefinerStrategy) {
                atRuleBuilder.add((AtRuleRefinerStrategy)strategy);
            } else if (strategy instanceof FunctionRefinerStrategy) {
                functionValueBuilder.add((FunctionRefinerStrategy)strategy);
            } else if (strategy instanceof DeclarationRefinerStrategy) {
                declarationBuilder.add((DeclarationRefinerStrategy)strategy);
            } else if (strategy instanceof SelectorRefinerStrategy) {
                selectorBuilder.add((SelectorRefinerStrategy)strategy);
            }
        }

        this.atRuleRefiners = atRuleBuilder.build();
        this.selectorRefiners = selectorBuilder.build();
        this.declarationRefiners = declarationBuilder.build();
        this.functionRefiners = functionValueBuilder.build();
    }

    /**
     * Refines an {@link AtRule} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(AtRule, Broadcaster, Refiner)} will be
     * used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link AtRule#refine()} instead.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(AtRule atRule) {
        return refine(atRule, defaultBroadcaster);
    }

    /**
     * Refines an {@link AtRule} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(AtRule, Broadcaster, Refiner)} will be
     * used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link AtRule#refine()} instead.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     * @param broadcaster
     *     {@link Broadcaster} to use.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(AtRule atRule, Broadcaster broadcaster) {
        for (AtRuleRefinerStrategy strategy : atRuleRefiners) {
            if (strategy.refine(atRule, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(atRule, broadcaster, this);
    }

    /**
     * Refines a {@link Selector} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(Selector, Broadcaster, Refiner)} will be
     * used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Selector#refine()} instead.
     *
     * @param selector
     *     The {@link Selector} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(Selector selector) {
        return refine(selector, defaultBroadcaster);
    }

    /**
     * Refines a {@link Selector} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(Selector, Broadcaster, Refiner)} will be
     * used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Selector#refine()} instead.
     *
     * @param selector
     *     The {@link Selector} to refine.
     * @param broadcaster
     *     {@link Broadcaster} to use.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(Selector selector, Broadcaster broadcaster) {
        for (SelectorRefinerStrategy strategy : selectorRefiners) {
            if (strategy.refine(selector, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(selector, broadcaster, this);
    }

    /**
     * Refines a {@link Declaration} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(Declaration, Broadcaster, Refiner)} will
     * be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Declaration#refine()} instead.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(Declaration declaration) {
        return refine(declaration, defaultBroadcaster);
    }

    /**
     * Refines a {@link Declaration} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(Declaration, Broadcaster, Refiner)} will
     * be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Declaration#refine()} instead.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     * @param broadcaster
     *     {@link Broadcaster} to use.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(Declaration declaration, Broadcaster broadcaster) {
        for (DeclarationRefinerStrategy strategy : declarationRefiners) {
            if (strategy.refine(declaration, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(declaration, broadcaster, this);
    }

    /**
     * Refines a {@link GenericFunctionValue} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(RawFunction, Broadcaster, Refiner)} will
     * be used.
     *
     * @param raw
     *     The {@link RawFunction} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(RawFunction raw) {
        return refine(raw, defaultBroadcaster);
    }

    /**
     * Refines a {@link GenericFunctionValue} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(RawFunction, Broadcaster, Refiner)} will
     * be used.
     *
     * @param raw
     *     The {@link RawFunction} to refine.
     * @param broadcaster
     *     {@link Broadcaster} to use.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(RawFunction raw, Broadcaster broadcaster) {
        for (FunctionRefinerStrategy strategy : functionRefiners) {
            if (strategy.refine(raw, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(raw, broadcaster, this);
    }

    /**
     * Gets the {@link Broadcaster} registered with this {@link Refiner}.
     *
     * @return The {@link Broadcaster}.
     */
    public Broadcaster broadcaster() {
        return defaultBroadcaster;
    }
}

