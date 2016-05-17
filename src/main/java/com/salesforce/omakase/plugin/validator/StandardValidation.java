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

package com.salesforce.omakase.plugin.validator;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

/**
 * Enables all standard library-provided validations.
 * <p>
 * This includes auto-refinement of every {@link Refinable} instance plus:
 * <p>
 * {@link PseudoElementValidator}, (more to come).
 *
 * @author nmcwilliams
 */
public final class StandardValidation implements DependentPlugin {
    private final boolean autoRefine;

    /**
     * Creates a new {@link StandardValidation} instance that will also add an {@link AutoRefiner} with {@link
     * AutoRefiner#all()}.
     */
    public StandardValidation() {
        this(true);
    }

    /**
     * Creates a new {@link StandardValidation} instance that will also add an {@link AutoRefiner} with {@link AutoRefiner#all()}
     * as specified.
     *
     * @param autoRefine
     *     Whether to also include auto-refinement of everything.
     */
    public StandardValidation(boolean autoRefine) {
        this.autoRefine = autoRefine;
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        if (autoRefine) {
            registry.require(AutoRefiner.class).all();
        }

        registry.require(PseudoElementValidator.class);
    }
}
