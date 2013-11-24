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

package com.salesforce.omakase.plugin.validator;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

/**
 * Enables all standard library-provided validations.
 * <p/>
 * This includes auto-refinement of every {@link Refinable} instance plus:
 * <p/>
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
