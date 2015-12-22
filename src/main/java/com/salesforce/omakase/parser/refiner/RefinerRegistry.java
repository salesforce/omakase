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
