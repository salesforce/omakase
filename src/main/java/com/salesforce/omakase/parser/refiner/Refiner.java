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
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.List;

/**
 * Responsible for refining {@link Refinable} objects, such as {@link Selector} and {@link Declaration}.
 * <p/>
 * A list of {@link RefinerStrategy} objects can be registered to customize and extends the standard CSS syntax. See the readme
 * file for more details.
 *
 * @author nmcwilliams
 * @see SyntaxPlugin
 */
public final class Refiner {
    private static final StandardRefinerStrategy STANDARD = new StandardRefinerStrategy();

    private final Broadcaster broadcaster;
    private final List<AtRuleRefinerStrategy> atRuleRefiners;
    private final List<SelectorRefinerStrategy> selectorRefiners;
    private final List<DeclarationRefinerStrategy> declarationRefiners;
    private final List<FunctionValueRefinerStrategy> functionValueRefiners;

    /**
     * Creates a new {@link Refiner} instance with the given {@link Broadcaster} to use for new refined AST objects.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     */
    public Refiner(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;

        this.atRuleRefiners = ImmutableList.of();
        this.selectorRefiners = ImmutableList.of();
        this.declarationRefiners = ImmutableList.of();
        this.functionValueRefiners = ImmutableList.of();
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
        this.broadcaster = broadcaster;

        // ladies and gentlemen, consumer convenience over efficiency and performance
        ImmutableList.Builder<AtRuleRefinerStrategy> atRuleBuilder = ImmutableList.builder();
        ImmutableList.Builder<SelectorRefinerStrategy> selectorBuilder = ImmutableList.builder();
        ImmutableList.Builder<DeclarationRefinerStrategy> declarationBuilder = ImmutableList.builder();
        ImmutableList.Builder<FunctionValueRefinerStrategy> functionValueBuilder = ImmutableList.builder();

        for (RefinerStrategy strategy : strategies) {
            if (strategy instanceof AtRuleRefinerStrategy) {
                atRuleBuilder.add((AtRuleRefinerStrategy)strategy);
            } else if (strategy instanceof FunctionValueRefinerStrategy) {
                functionValueBuilder.add((FunctionValueRefinerStrategy)strategy);
            } else if (strategy instanceof DeclarationRefinerStrategy) {
                declarationBuilder.add((DeclarationRefinerStrategy)strategy);
            } else if (strategy instanceof SelectorRefinerStrategy) {
                selectorBuilder.add((SelectorRefinerStrategy)strategy);
            }
        }

        this.atRuleRefiners = atRuleBuilder.build();
        this.selectorRefiners = selectorBuilder.build();
        this.declarationRefiners = declarationBuilder.build();
        this.functionValueRefiners = functionValueBuilder.build();
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
        for (DeclarationRefinerStrategy strategy : declarationRefiners) {
            if (strategy.refine(declaration, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(declaration, broadcaster, this);
    }

    /**
     * Refines a {@link FunctionValue} object.
     * <p/>
     * {@link RefinerStrategy} objects will be consulted in the registered order. If no {@link RefinerStrategy} decides to handle
     * the instance, or if none are registered then {@link StandardRefinerStrategy#refine(Declaration, Broadcaster, Refiner)} will
     * be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link FunctionValue#refine()} instead.
     *
     * @param functionValue
     *     The {@link FunctionValue} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public boolean refine(FunctionValue functionValue) {
        for (FunctionValueRefinerStrategy strategy : functionValueRefiners) {
            if (strategy.refine(functionValue, broadcaster, this)) return true;
        }

        // fallback to the default refiner
        return STANDARD.refine(functionValue, broadcaster, this);
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

