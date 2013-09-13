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

import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.selector.CombinatorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_SELECTOR;

/**
 * TESTME
 * <p/>
 * Represents a CSS combinator.
 *
 * @author nmcwilliams
 * @see CombinatorParser
 */
@Subscribable
@Description(value = "combinator segment", broadcasted = REFINED_SELECTOR)
public class Combinator extends AbstractGroupable<SelectorPart> implements SelectorPart {
    private final CombinatorType type;

    /**
     * Creates a new instance with the given line and column numbers, and the {@link CombinatorType}.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param type
     *     The {@link CombinatorType}.
     */
    public Combinator(int line, int column, CombinatorType type) {
        super(line, column);
        this.type = type;
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param type
     *     The {@link CombinatorType}.
     */
    public Combinator(CombinatorType type) {
        this.type = checkNotNull(type, "type cannot be null");
    }

    @Override
    public boolean isSelector() {
        return false;
    }

    @Override
    public boolean isCombinator() {
        return true;
    }

    @Override
    public SelectorPartType type() {
        switch (type) {
        case DESCENDANT:
            return SelectorPartType.DESCENDANT_COMBINATOR;
        case CHILD:
            return SelectorPartType.CHILD_COMBINATOR;
        case ADJACENT_SIBLING:
            return SelectorPartType.ADJACENT_SIBLING_COMBINATOR;
        case GENERAL_SIBLING:
            return SelectorPartType.GENERAL_SIBLING_COMBINATOR;
        }
        throw new RuntimeException("unknown combinator type");
    }

    @Override
    protected SelectorPart self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        switch (type) {
        case DESCENDANT:
            appendable.append(' ');
            break;
        case CHILD:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('>');
            appendable.spaceIf(writer.isVerbose());
            break;
        case ADJACENT_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('+');
            appendable.spaceIf(writer.isVerbose());
            break;
        case GENERAL_SIBLING:
            appendable.spaceIf(writer.isVerbose());
            appendable.append('~');
            appendable.spaceIf(writer.isVerbose());
            break;
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("type", type)
            .toString();
    }
}
