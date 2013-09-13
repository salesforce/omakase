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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;

import java.util.List;

/**
 * TESTME
 * <p/>
 * Base class for {@link SelectorPart}s.
 */
public abstract class AbstractSelectorPart extends AbstractGroupable<Selector, SelectorPart> implements SelectorPart {
    /** Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units). */
    public AbstractSelectorPart() {
    }

    /**
     * Creates a new instance with the given line and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     */
    public AbstractSelectorPart(int line, int column) {
        super(line, column);
    }

    @Override
    public Selector parentSelector() {
        return parent();
    }

    @Override
    public List<Comment> comments() {
        if (!isFirst()) return super.comments();

        // the first selector part should also include the comments included on the selector
        return ImmutableList.copyOf(Iterables.concat(parentSelector().comments(), super.comments()));
    }
}
