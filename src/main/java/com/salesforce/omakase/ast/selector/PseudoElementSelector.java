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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.Named;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Represents a CSS pseudo element selector.
 *
 * @author nmcwilliams
 * @see PseudoSelectorParser
 */
@Subscribable
@Description(value = "pseudo element selector segment", broadcasted = REFINED_SELECTOR)
public final class PseudoElementSelector extends AbstractSelectorPart implements SimpleSelector, Named {
    /** these can use pseudo class syntax but are actually pseudo elements */
    public static final Set<String> POSERS = Sets.newHashSet("first-line", "first-letter", "before", "after");

    private String name;

    /**
     * Constructs a new {@link PseudoElementSelector} selector with the given name.
     * <p>
     * If dynamically creating a new instance then use {@link #PseudoElementSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the pseudo element.
     */
    public PseudoElementSelector(int line, int column, String name) {
        super(line, column);
        this.name = name.toLowerCase();
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the pseudo element.
     */
    public PseudoElementSelector(String name) {
        name(name);
    }

    /**
     * Constructor to use when you know for certain that the given name is already lower-cased.
     *
     * @param name
     *     Name of the pseudo element.
     * @param knownLowerCase
     *     Specify true to indicate the name is already lower-cased.
     */
    public PseudoElementSelector(String name, boolean knownLowerCase) {
        if (knownLowerCase) {
            this.name = name;
        } else {
            name(name);
        }
    }

    /**
     * Sets the name of the selector.
     *
     * @param name
     *     The new name.
     *
     * @return this, for chaining.
     */
    public PseudoElementSelector name(String name) {
        checkNotNull(name, "name cannot be null");
        this.name = name.toLowerCase();
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.PSEUDO_ELEMENT_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(POSERS.contains(name) ? ":" : "::").append(name);
    }

    @Override
    public PseudoElementSelector copy() {
        return new PseudoElementSelector(name, true).copiedFrom(this);
    }
}
