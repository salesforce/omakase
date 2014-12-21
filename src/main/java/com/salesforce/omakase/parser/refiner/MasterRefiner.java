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

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.AtRuleBlock;
import com.salesforce.omakase.ast.atrule.AtRuleExpression;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.token.StandardTokenFactory;
import com.salesforce.omakase.parser.token.TokenFactory;
import com.salesforce.omakase.plugin.SyntaxPlugin;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Top-level manager/registry used for refining {@link Refinable} objects, such as {@link Selector} and {@link Declaration}, and
 * other "refinable" syntax such as custom functions.
 *
 * @author nmcwilliams
 * @see SyntaxPlugin
 * @see RefinerRegistry
 */
public final class MasterRefiner implements Refiner, RefinerRegistry {
    private static final StandardRefiner STANDARD = new StandardRefiner();

    private final Broadcaster defaultBroadcaster;
    private final TokenFactory tokenFactory;

    private final List<AtRuleRefiner> atRuleRefiners = new ArrayList<>();
    private final List<SelectorRefiner> selectorRefiners = new ArrayList<>();
    private final List<DeclarationRefiner> declarationRefiners = new ArrayList<>();
    private final List<FunctionRefiner> functionRefiners = new ArrayList<>();

    /**
     * Creates a new {@link MasterRefiner} instance without a specific {@link Broadcaster} specified.
     * <p/>
     * Generally not the constructor to use.
     */
    public MasterRefiner() {
        this(new QueryableBroadcaster());
    }

    /**
     * Creates a new {@link MasterRefiner} instance with the given {@link Broadcaster} to use for new refined AST objects.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     */
    public MasterRefiner(Broadcaster broadcaster) {
        this(broadcaster, StandardTokenFactory.instance());
    }

    /**
     * Creates a new {@link MasterRefiner} instance with the given {@link Broadcaster} to use for new refined AST objects. The
     * given {@link TokenFactory} is used for grammar token delimiters.
     *
     * @param broadcaster
     *     The {@link Broadcaster} to use for refined AST objects.
     * @param tokenFactory
     *     The {@link TokenFactory} parsers should use.
     */
    public MasterRefiner(Broadcaster broadcaster, TokenFactory tokenFactory) {
        this.defaultBroadcaster = broadcaster;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public MasterRefiner register(AtRuleRefiner refiner) {
        atRuleRefiners.add(checkNotNull(refiner, "refiner cannot be null"));
        return this;
    }

    @Override
    public MasterRefiner register(SelectorRefiner refiner) {
        selectorRefiners.add(checkNotNull(refiner, "refiner cannot be null"));
        return this;
    }

    @Override
    public MasterRefiner register(DeclarationRefiner refiner) {
        declarationRefiners.add(checkNotNull(refiner, "refiner cannot be null"));
        return this;
    }

    @Override
    public MasterRefiner register(FunctionRefiner refiner) {
        functionRefiners.add(checkNotNull(refiner, "refiner cannot be null"));
        return this;
    }

    @Override
    public MasterRefiner registerMulti(Refiner refiner) {
        if (refiner instanceof AtRuleRefiner) {
            atRuleRefiners.add((AtRuleRefiner)refiner);
        }
        if (refiner instanceof FunctionRefiner) {
            functionRefiners.add((FunctionRefiner)refiner);
        }
        if (refiner instanceof DeclarationRefiner) {
            declarationRefiners.add((DeclarationRefiner)refiner);
        }
        if (refiner instanceof SelectorRefiner) {
            selectorRefiners.add((SelectorRefiner)refiner);
        }
        return this;
    }

    /**
     * Refines an {@link AtRule} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(AtRule, Broadcaster, MasterRefiner)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link AtRule#refine()} instead.
     *
     * @param atRule
     *     The {@link AtRule} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public Refinement refine(AtRule atRule) {
        return refine(atRule, defaultBroadcaster);
    }

    /**
     * Refines an {@link AtRule} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(AtRule, Broadcaster, MasterRefiner)} will be used.
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
    public Refinement refine(AtRule atRule, Broadcaster broadcaster) {
        Refinement refinement = Refinement.NONE;

        // tru the custom refiners
        for (AtRuleRefiner strategy : atRuleRefiners) {
            Refinement result = refine(atRule, broadcaster, strategy);
            if (result == Refinement.FULL) {
                return Refinement.FULL;
            } else if (result == Refinement.PARTIAL) {
                refinement = Refinement.PARTIAL;
            }
        }

        // fallback to the default refiner (refinement is currently either partial or none)
        Refinement standard = refine(atRule, broadcaster, STANDARD);
        return (standard == Refinement.FULL) ? Refinement.FULL : refinement;
    }

    /** attempts to refine an at-rule using the given refiner */
    private Refinement refine(AtRule atRule, Broadcaster broadcaster, AtRuleRefiner refiner) {
        // use a queue so that we can hold off on broadcasting the the expression or block until we can automatically associate
        // it with the parent at rule
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        Refinement result = refiner.refine(atRule, queryable, this);
        if (result == Refinement.FULL || result == Refinement.PARTIAL) {
            // automatically add the expression if it was broadcasted
            Optional<AtRuleExpression> expression = queryable.find(AtRuleExpression.class);
            if (expression.isPresent()) {
                atRule.expression(expression.get());
            }

            // automatically add the block if it was broadcasted
            Optional<AtRuleBlock> block = queryable.find(AtRuleBlock.class);
            if (block.isPresent()) {
                atRule.block(block.get());
            }

            // once they are all added we're good to send them out
            queue.resume();
            return result;
        }

        return Refinement.NONE;
    }

    /**
     * Refines a {@link Selector} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(Selector, Broadcaster, MasterRefiner)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Selector#refine()} instead.
     *
     * @param selector
     *     The {@link Selector} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public Refinement refine(Selector selector) {
        return refine(selector, defaultBroadcaster);
    }

    /**
     * Refines a {@link Selector} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(Selector, Broadcaster, MasterRefiner)} will be used.
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
    public Refinement refine(Selector selector, Broadcaster broadcaster) {
        Refinement refinement = Refinement.NONE;

        // try the custom refiners
        for (SelectorRefiner strategy : selectorRefiners) {
            Refinement result = refine(selector, broadcaster, strategy);
            if (result == Refinement.FULL) {
                return Refinement.FULL;
            } else if (result == Refinement.PARTIAL) {
                refinement = Refinement.PARTIAL;
            }
        }

        // fallback to the default refiner (refinement is currently either partial or none)
        Refinement standard = refine(selector, broadcaster, STANDARD);
        return (standard == Refinement.FULL) ? Refinement.FULL : refinement;
    }

    /** attempts to refine a selector using the given refiner */
    private Refinement refine(Selector selector, Broadcaster broadcaster, SelectorRefiner refiner) {
        // use a queue so that we can hold off on broadcasting the individual parts until we have them all. This makes rework
        // plugins that utilize order (#isFirst(), etc...) work smoothly.
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();
        QueryableBroadcaster queryable = new QueryableBroadcaster(queue);

        Refinement result = refiner.refine(selector, queryable, this);
        if (result == Refinement.FULL || result == Refinement.PARTIAL) {
            // store the parsed selector parts
            selector.appendAll(queryable.filter(SelectorPart.class));

            // once they are all added we're good to send them out
            queue.resume();
            return result;
        }

        return Refinement.NONE;
    }

    /**
     * Refines a {@link Declaration} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(Declaration, Broadcaster, MasterRefiner)} will be used.
     * <p/>
     * <b>Note:</b> Non-library code usually should not call this method directly, but {@link Declaration#refine()} instead.
     *
     * @param declaration
     *     The {@link Declaration} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public Refinement refine(Declaration declaration) {
        return refine(declaration, defaultBroadcaster);
    }

    /**
     * Refines a {@link Declaration} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(Declaration, Broadcaster, MasterRefiner)} will be used.
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
    public Refinement refine(Declaration declaration, Broadcaster broadcaster) {
        Refinement refinement = Refinement.NONE;

        // try the custom refiners
        for (DeclarationRefiner strategy : declarationRefiners) {
            Refinement result = refine(declaration, broadcaster, strategy);
            if (result == Refinement.FULL) {
                return Refinement.FULL;
            } else if (result == Refinement.PARTIAL) {
                refinement = Refinement.PARTIAL;
            }

        }

        // fallback to the default refiner (refinement is currently either partial or none)
        Refinement standard = refine(declaration, broadcaster, STANDARD);
        return (standard == Refinement.FULL) ? Refinement.FULL : refinement;
    }

    /** attempts to refine a declaration using the given refiner */
    private Refinement refine(Declaration declaration, Broadcaster broadcaster, DeclarationRefiner refiner) {
        // using a queue so that we can link everything together before terms, etc... are emitted
        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause().alwaysFlush(RawFunction.class);
        SingleInterestBroadcaster<PropertyValue> single = SingleInterestBroadcaster.of(PropertyValue.class, queue);

        Refinement result = refiner.refine(declaration, single, this);
        if (result == Refinement.FULL || result == Refinement.PARTIAL) {
            // store the parsed value
            Optional<PropertyValue> value = single.broadcasted();
            if (!value.isPresent()) throw new ParserException(declaration, Message.BAD_DECLARATION_REFINER, refiner);
            declaration.propertyValue(value.get());

            // everything is linked so send the broadcasts out
            queue.resume();
            return result;
        }

        return Refinement.NONE;
    }

    /**
     * Refines a {@link GenericFunctionValue} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(RawFunction, Broadcaster, MasterRefiner)} will be used.
     *
     * @param raw
     *     The {@link RawFunction} to refine.
     *
     * @return Whether refinement occurred or not.
     */
    public Refinement refine(RawFunction raw) {
        return refine(raw, defaultBroadcaster);
    }

    /**
     * Refines a {@link GenericFunctionValue} object.
     * <p/>
     * {@link Refiner} objects will be consulted in the registered order. If no {@link Refiner} decides to handle the instance, or
     * if none are registered then {@link StandardRefiner#refine(RawFunction, Broadcaster, MasterRefiner)} will be used.
     *
     * @param raw
     *     The {@link RawFunction} to refine.
     * @param broadcaster
     *     {@link Broadcaster} to use.
     *
     * @return Whether refinement occurred or not.
     */
    public Refinement refine(RawFunction raw, Broadcaster broadcaster) {
        // try the custom refiners
        for (FunctionRefiner strategy : functionRefiners) {
            Refinement result = strategy.refine(raw, broadcaster, this);
            if (result == Refinement.FULL) {
                return Refinement.FULL;
            } else if (result == Refinement.PARTIAL) {
                throw new UnsupportedOperationException("Partial refinement of RawFunctions is not supported");
            }
        }

        // fallback to the default refiner
        return STANDARD.refine(raw, broadcaster, this);
    }

    /**
     * Gets the {@link Broadcaster} registered with this {@link MasterRefiner}.
     *
     * @return The {@link Broadcaster}.
     */
    public Broadcaster broadcaster() {
        return defaultBroadcaster;
    }

    /**
     * Gets the {@link TokenFactory} registered with this {@link MasterRefiner}.
     *
     * @return The {@link TokenFactory}.
     */
    public TokenFactory tokenFactory() {
        return tokenFactory;
    }
}

