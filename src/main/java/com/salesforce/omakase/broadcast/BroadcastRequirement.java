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

package com.salesforce.omakase.broadcast;

import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.basic.AutoRefiner;

/**
 * Indicates the conditions for an {@link Subscribable} type to be broadcasted.
 *
 * @author nmcwilliams
 */
public enum BroadcastRequirement {
    /** automatically broadcasted (except for Syntax, where some syntax items are only broadcasted during refinement) */
    AUTOMATIC("Automatic"),

    /** refine must be called on {@link Selector}s, e.g., with {@link AutoRefiner#selectors()} */
    REFINED_SELECTOR("Selector#refine"),

    /** refine must be called on {@link Declaration}s, e.g., with {@link AutoRefiner#declarations()} */
    REFINED_DECLARATION("Declaration#refine"),

    /** refine must be called on {@link AtRule}s, e.g., with {@link AutoRefiner#atRules()} */
    REFINED_AT_RULE("AtRule#refine"),

    /** Requires special conditions to be broadcasted */
    SPECIAL("Under certain conditions*");

    private final String description;

    private BroadcastRequirement(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
