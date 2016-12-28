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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * A {@link SimpleSelector}, {@link PseudoElementSelector}, or {@link Combinator}.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "group interface for all selector segments", broadcasted = REFINED_SELECTOR)
public interface SelectorPart extends Groupable<Selector, SelectorPart> {
    /**
     * Gets the parent {@link Selector} instance.
     * <p>
     * Take this CSS snippet for example:
     * <p>
     * {@code .class > .class2 #id, p a}
     * <p>
     * There are two {@link Selector}s, The first being {@code .class > .class2 #id}, the second being {@code p a}.
     * <p>
     * For the {@link SelectorPart} {@code #id}, which is an {@link IdSelector}, calling this method will return the first {@link
     * Selector} (with {@code .class > .class2 #id}).
     *
     * @return The parent.
     */
    @Override
    Selector parent(); // overridden for docs

    /**
     * Gets the {@link SelectorPartType} of this {@link SelectorPart}. This is a delegate to {@link Groupable#parent()}.
     *
     * @return The {@link SelectorPartType}.
     */
    SelectorPartType type();

    @Override
    SelectorPart copy();
}
