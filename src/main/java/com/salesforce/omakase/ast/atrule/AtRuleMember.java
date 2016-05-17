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

package com.salesforce.omakase.ast.atrule;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;

/**
 * Either an {@link AtRuleExpression} or an {@link AtRuleBlock}.
 *
 * @author nmcwilliams
 */
public interface AtRuleMember extends Syntax {
    /**
     * Sets the parent {@link AtRule}. Generally this is handled automatically when this block is set on the {@link AtRule}, so it
     * is not recommended to call this method manually. If you do, results may be unexpected.
     * <p>
     * Do not use this method to move a block from one parent to another.
     *
     * @param atRule
     *     The parent at-rule.
     *
     * @return this, for chaining.
     *
     * @see AbstractAtRuleMember
     */
    AtRuleMember parent(AtRule atRule);

    /**
     * Gets the parent {@link AtRule} that owns this property, or absent if not set. This will not be set for dynamically created
     * property values not yet added to an {@link AtRule} instance.
     *
     * @return The parent {@link AtRule}, or {@link Optional#absent()} if not set.
     */
    AtRule parent();
}
