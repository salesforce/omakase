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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.refiner.GenericRefiner;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

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
public final class Selector extends AbstractGroupable<Rule, Selector> implements Refinable<Selector> {
    private final SyntaxCollection<Selector, SelectorPart> parts;
    private final RawSyntax rawContent;
    private final transient GenericRefiner refiner;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content. This selector can be further refined to the
     * individual {@link SelectorPart}s by using {@link #refine()}.
     * <p/>
     * If dynamically creating a new instance then use {@link #Selector(SelectorPart...)} or {@link #Selector(Iterable)} instead.
     *
     * @param rawContent
     *     The selector content.
     * @param refiner
     *     The {@link GenericRefiner} to be used later during refinement of this object.
     */
    public Selector(RawSyntax rawContent, GenericRefiner refiner) {
        super(rawContent.line(), rawContent.column());
        this.refiner = refiner;
        this.rawContent = rawContent;
        this.parts = new LinkedSyntaxCollection<Selector, SelectorPart>(this, refiner.broadcaster());
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
        this(-1, -1, parts);
    }

    /**
     * Creates a new instance with the given line, number, and parts.
     * <p/>
     * If dynamically creating a new instance then use {@link #Selector(SelectorPart...)} or {@link #Selector(Iterable)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param parts
     *     The selector parts to add.
     */
    public Selector(int line, int column, SelectorPart... parts) {
        this(line, column, Lists.newArrayList(parts));
    }

    /**
     * Creates a new instance with the given line, number, and parts.
     * <p/>
     * If dynamically creating a new instance then use {@link #Selector(SelectorPart...)} or {@link #Selector(Iterable)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param parts
     *     The selector parts to add.
     */
    public Selector(int line, int column, Iterable<SelectorPart> parts) {
        super(line, column);
        this.refiner = null;
        this.rawContent = null;
        this.parts = new LinkedSyntaxCollection<Selector, SelectorPart>(this).appendAll(parts);
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

    /**
     * Gets whether this selector is used for @keyframes.
     * <p/>
     * XXX consider making keyframe selectors disjoint from regular selectors.
     *
     * @return Whether this selector is used for keyframes.
     */
    public boolean isKeyframe() {
        for (SelectorPart part : parts) {
            if (part instanceof KeyframeSelector) return true;
        }
        return false;
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
                writer.writeInner(part, appendable);
            }
        } else {
            writer.writeInner(rawContent, appendable);
        }
    }

    @Override
    protected Selector makeCopy(Prefix prefix, SupportMatrix support) {
        List<SelectorPart> copiedParts = Lists.newArrayList();

        for (SelectorPart part : parts) {
            copiedParts.add(part.copy(prefix, support));
        }

        return new Selector(copiedParts);
    }
}
