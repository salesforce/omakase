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

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.Refiner;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents a CSS selector.
 * <p/>
 * {@link Selector}s are lists of {@link SelectorPart}s. Individual {@link Selector}s are separated by commas. For example, in
 * <pre>
 * {@code .class, .class #id}
 * </pre>
 * There are two selectors:
 * <pre>
 * 1: {@code .class}
 * 2: {@code .class #id}
 * </pre>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform basic
 * grammar validation. See the notes on {@link Refinable}.
 * <p/>
 * When dynamically creating selectors, you usually pass in the various {@link SelectorPart}s to the constructor. Example:
 * <pre>
 * {@code Selector selector = new Selector(new ClassSelector("myClass"), Combinator.descendant(), new IdSelector("myId"));}
 * </pre>
 * You can also append or prepend parts directly on the {@link SyntaxCollection} returned from the {@link #parts()} method.
 *
 * @author nmcwilliams
 * @see ComplexSelectorParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Selector extends AbstractGroupable<Rule, Selector> implements Refinable<Selector> {
    private final SyntaxCollection<Selector, SelectorPart> parts;
    private final RawSyntax rawContent;
    private final Refiner refiner;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content. This selector can be further refined to the
     * individual {@link SelectorPart}s by using {@link #refine()}.
     * <p/>
     * If dynamically creating a new instance then use {@link #Selector(SelectorPart...)} or {@link #Selector(Iterable)} instead.
     *
     * @param rawContent
     *     The selector content.
     * @param refiner
     *     The {@link Refiner} to be used later during refinement of this object.
     */
    public Selector(RawSyntax rawContent, Refiner refiner) {
        super(rawContent.line(), rawContent.column());

        this.refiner = refiner;
        this.rawContent = rawContent;
        this.parts = StandardSyntaxCollection.create(this, refiner.broadcaster());
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param parts
     *     The parts within the selector.
     */
    public Selector(SelectorPart... parts) {
        this(Lists.newArrayList(parts));
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param parts
     *     The parts within the selector.
     */
    public Selector(Iterable<SelectorPart> parts) {
        this.refiner = null;
        this.rawContent = null;
        this.parts = StandardSyntaxCollection.create(this);
        this.parts.appendAll(parts);
    }

    /**
     * Gets the original, raw, non-validated selector content.
     *
     * @return The raw selector content.
     */
    public RawSyntax rawContent() {
        return rawContent;
    }

    /**
     * Gets the individual parts of the selector. The selector will be automatically refined if not done so already.
     *
     * @return The list of {@link SelectorPart} members.
     */
    public SyntaxCollection<Selector, SelectorPart> parts() {
        return refine().parts;
    }

    /**
     * Appends all of the given parts to this {@link Selector}.
     *
     * @param newParts
     *     The parts to append.
     *
     * @return this, for chaining.
     */
    public Selector appendAll(Iterable<SelectorPart> newParts) {
        parts.appendAll(newParts);
        return this;
    }

    @Override
    public boolean isRefined() {
        return !parts.isEmpty();
    }

    @Override
    public Selector refine() {
        if (!isRefined() && refiner != null) {
            refiner.refine(this);
        }

        return this;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        parts.propagateBroadcast(broadcaster);
    }

    @Override
    protected Selector self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isRefined()) {
            for (SelectorPart part : parts) {
                writer.write(part, appendable);
            }
        } else {
            writer.write(rawContent, appendable);
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("abstract", super.toString())
            .add("raw", rawContent)
            .add("parts", parts)
            .addUnlessEmpty("orphaned", orphanedComments())
            .toString();
    }
}
