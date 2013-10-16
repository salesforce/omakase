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

    /** a descendant combinator */
    DESCENDANT_COMBINATOR(true),

    /** a child combinator */
    CHILD_COMBINATOR(true),

    /** an adjacent sibling combinator */
    ADJACENT_SIBLING_COMBINATOR(true),

    /** a general sibling combinator */
    GENERAL_SIBLING_COMBINATOR(true),

    /** a comment that is not associated with another syntax unit */
    ORPHANED_COMMENT;

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
