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

/**
 * The type of selector or combinator for a {@link SelectorPart}. Checking against {@link SelectorPart#type()} can be
 * useful in place of using <pre>instanceof</pre>.
 *
 * @author nmcwilliams
 */
public enum SelectorPartType {
    /** a universal selector */
    UNIVERSAL_SELECTOR,

    /** a type (aka element) selector */
    TYPE_SELECTOR,

    /** an id selector */
    ID_SELECTOR,

    /** a class selector */
    CLASS_SELECTOR,

    /** an attribute selector */
    ATTRIBUTE_SELECTOR,

    /** a pseudo class selector */
    PSEUDO_CLASS_SELECTOR,

    /** a pseudo element selector */
    PSEUDO_ELEMENT_SELECTOR,

    /** keyframes selector */
    KEYFRAMES_SELECTOR,

    /** a descendant combinator */
    DESCENDANT_COMBINATOR(true),

    /** a child combinator */
    CHILD_COMBINATOR(true),

    /** an adjacent sibling combinator */
    ADJACENT_SIBLING_COMBINATOR(true),

    /** a general sibling combinator */
    GENERAL_SIBLING_COMBINATOR(true),

    /** a comment that is not associated with another syntax unit */
    ORPHANED_COMMENT,

    /** custom syntax */
    CUSTOM;

    private final boolean isCombinator;

    SelectorPartType() {
        this(false);
    }

    SelectorPartType(boolean isCombinator) {
        this.isCombinator = isCombinator;
    }

    /**
     * Gets whether this type represents a combinator.
     *
     * @return True if this type represents a combinator.
     */
    public boolean isCombinator() {
        return isCombinator;
    }
}
