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

package com.salesforce.omakase.ast.atrule;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.StatementIterable;

/**
 * The block of an {@link AtRule}.
 *
 * @author nmcwilliams
 */
public interface AtRuleBlock extends StatementIterable {
    /**
     * Sets the parent {@link AtRule}. Generally this is handled automatically when this block is set on the {@link AtRule}, so it
     * is not recommended to call this method manually. If you do, results may be unexpected.
     * <p/>
     * Do not use this method to move a block from one parent to another.
     *
     * @param atRule
     *     The parent at-rule.
     *
     * @return this, for chaining.
     *
     * @see AbstractAtRuleBlock
     */
    AtRuleBlock parent(AtRule atRule);

    /**
     * Gets the parent {@link AtRule} that owns this property, or absent if not set. This will not be set for dynamically created
     * property values not yet added to an {@link AtRule} instance.
     *
     * @return The parent {@link AtRule}, or {@link Optional#absent()} if not set.
     */
    Optional<AtRule> parent();

    @Override
    AtRuleBlock copy();
}
