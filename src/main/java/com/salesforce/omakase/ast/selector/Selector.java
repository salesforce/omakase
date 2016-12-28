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

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.plugin.core.AutoRefine;
import com.salesforce.omakase.plugin.core.StandardValidation;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.salesforce.omakase.broadcast.BroadcastRequirement.AUTOMATIC;

/**
 * Represents a CSS selector.
 * <p>
 * {@link Selector}s are lists of {@link SelectorPart}s. Individual {@link Selector}s are separated by commas. For example, in
 * <pre>
 * {@code .class, .class #id}
 * </pre>
 * There are two selectors:
 * <pre>
 * 1: {@code .class}
 * 2: {@code .class #id}
 * </pre>
 * See the notes on {@link Refinable} regarding unrefined selectors.
 * <p>
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
public final class Selector extends AbstractGroupable<Rule, Selector> implements Refinable {
    private final SyntaxCollection<Selector, SelectorPart> parts;
    private final RawSyntax raw;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content.
     * <p>
     * If dynamically creating a new instance then use {@link #Selector(SelectorPart...)} or {@link #Selector(Iterable)} instead.
     *  @param raw
     *     The selector content.
     *
     */
    public Selector(RawSyntax raw) {
        super(raw.line(), raw.column());
        this.raw = raw;
        this.parts = new LinkedSyntaxCollection<>(this);
        status(Status.RAW);
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
     * <p>
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
     * <p>
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
        this.raw = null;
        this.parts = new LinkedSyntaxCollection<Selector, SelectorPart>(this).appendAll(parts);
    }

    /**
     * Gets the original, raw, non-validated selector content.
     *
     * @return The raw selector content, or an empty {@link Optional} if the raw content is not set (e.g., a dynamically created
     * unit).
     */
    public Optional<RawSyntax> raw() {
        return Optional.ofNullable(raw);
    }

    /**
     * Gets the individual parts of the selector.
     * <p>
     * <b>Important:</b> this <b>may not contain</b> any selector parts if this selector is unrefined! See the main readme file
     * for more information on refinement.
     * <p>
     * For basic use cases, to ensure this is always refined and properly set use {@link AutoRefine} or {@link StandardValidation}
     * during parsing. For reasons why you would <em>not</em> want to do that see the main readme file.
     *
     * @return The list of {@link SelectorPart} members.
     */
    public SyntaxCollection<Selector, SelectorPart> parts() {
        return parts;
    }

    /**
     * Appends the given part to this {@link Selector}.
     *
     * @param newPart
     *     The part to append.
     *
     * @return this, for chaining.
     */
    public Selector append(SelectorPart newPart) {
        parts.append(newPart);
        return this;
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
     * <p>
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
        return raw == null || !parts.isEmpty();
    }

    @Override
    public boolean breakBroadcast(SubscriptionPhase phase) {
        return super.breakBroadcast(phase) || (phase == SubscriptionPhase.REFINE && isRefined());
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster, Status status) {
        if (status() == status) {
            parts.propagateBroadcast(broadcaster, status);
            super.propagateBroadcast(broadcaster, status);
        }
    }

    @Override
    protected Selector self() {
        return this;
    }

    @Override
    public boolean writesOwnComments() {
        return true;
    }

    @Override
    public boolean isWritable() {
        // if unrefined (and super returns true) we should always be written out
        return super.isWritable() && (!isRefined() || !parts.isEmptyOrNoneWritable());
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (!writer.isFirstAtCurrentDepth()) {
            appendable.append(',');
            appendable.spaceIf(!writer.isCompressed());
        }

        writer.appendComments(comments(), appendable);

        if (isRefined()) {
            for (SelectorPart part : parts) {
                writer.writeInner(part, appendable);
            }
        } else {
            writer.writeInner(raw, appendable);
        }
    }

    @Override
    public Selector copy() {
        List<SelectorPart> copiedParts = new ArrayList<>();

        for (SelectorPart part : parts) {
            copiedParts.add(part.copy());
        }

        return new Selector(copiedParts).copiedFrom(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        parts.destroyAll();
    }
}
