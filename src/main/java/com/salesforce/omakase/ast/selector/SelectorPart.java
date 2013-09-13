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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * A {@link SimpleSelector}, {@link PseudoElementSelector}, or {@link Combinator}.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "parent interface for all selector segments", broadcasted = REFINED_SELECTOR)
public interface SelectorPart extends Syntax, Groupable<SelectorPart> {

    /**
     * Gets whether this {@link SelectorPart} is a selector ({@link SimpleSelector} or {@link PseudoElementSelector}).
     *
     * @return True if this {@link SelectorPart} is a selector.
     */
    boolean isSelector();

    /**
     * Gets whether this {@link SelectorPart} is a {@link Combinator}.
     *
     * @return True if this {@link SelectorPart} is a {@link Combinator}.
     */
    boolean isCombinator();

    /**
     * Gets the {@link SelectorPartType} of this {@link SelectorPart}.
     *
     * @return The {@link SelectorPartType}.
     */
    SelectorPartType type();
}
