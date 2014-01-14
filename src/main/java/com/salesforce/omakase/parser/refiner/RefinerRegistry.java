package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Used to register custom {@link Refiner} strategies.
 * <p/>
 * An instance of this class is given to all {@link SyntaxPlugin}s at the appropriate time. {@link SyntaxPlugin}s can register
 * custom syntax extensions for various parts of the CSS including functions, at-rules, etc... For more information on creating
 * refiners, see the main readme file.
 * <p/>
 * If your {@link Refiner} handles more than one type, use {@link #registerMulti(Refiner)}.
 *
 * @author nmcwilliams
 */
public interface RefinerRegistry {
    /**
     * Registers an {@link AtRuleRefiner} instance.
     *
     * @param refiner
     *     The {@link AtRuleRefiner}.
     *
     * @return this, for chaining.
     */
    RefinerRegistry register(AtRuleRefiner refiner);

    /**
     * Registers a {@link SelectorRefiner} instance.
     *
     * @param refiner
     *     The {@link SelectorRefiner}.
     *
     * @return this, for chaining.
     */
    RefinerRegistry register(SelectorRefiner refiner);

    /**
     * Registers a {@link DeclarationRefiner} instance.
     *
     * @param refiner
     *     The {@link DeclarationRefiner}.
     *
     * @return this, for chaining.
     */
    RefinerRegistry register(DeclarationRefiner refiner);

    /**
     * Registers a {@link FunctionRefiner} instance.
     *
     * @param refiner
     *     The {@link FunctionRefiner}.
     *
     * @return this, for chaining.
     */
    RefinerRegistry register(FunctionRefiner refiner);

    /**
     * Registers a {@link Refiner} that implements more than one of the {@link Refiner} interfaces.
     *
     * @param refiner
     *     The {@link Refiner} instance.
     *
     * @return this, for chaining.
     */
    RefinerRegistry registerMulti(Refiner refiner);
}
