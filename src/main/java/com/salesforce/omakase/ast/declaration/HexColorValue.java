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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A hex color value (e.g., "fffeee"). The value is always converted to lower-case.
 *
 * @author nmcwilliams
 * @see HexColorValue
 */
@Subscribable
@Description(value = "individual hex color value", broadcasted = REFINED_DECLARATION)
public final class HexColorValue extends AbstractTerm {
    private String color;

    /**
     * Constructs a new instance of a {@link HexColorValue}.
     * <p>
     * If dynamically creating a new instance then use {@link #HexColorValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param color
     *     The hex color (do not include the #). Automatically lower-cased.
     */
    public HexColorValue(int line, int column, String color) {
        super(line, column);
        this.color = color.toLowerCase();
    }

    /**
     * Constructs a new instance of a {@link HexColorValue} (used for dynamically created {@link Syntax} units).
     *
     * @param color
     *     The hex color (do not include the #).
     */
    public HexColorValue(String color) {
        color(color);
    }

    /**
     * Constructs a new instance of a {@link HexColorValue} (used for dynamically created {@link Syntax} units). Only use this
     * when you already know the string is lower-cased and doesn't start with a '#'.
     *
     * @param color
     *     The hex color (do not include the #).
     * @param lowerCaseAndNoHex
     *     Specifies whether the string is already lower-cased and doesn't start with a '#'.
     */
    public HexColorValue(String color, boolean lowerCaseAndNoHex) {
        if (lowerCaseAndNoHex) {
            this.color = color;
        } else {
            color(color);
        }
    }

    /**
     * Sets the value of the color (will be converted to lower-case).
     *
     * @param color
     *     The hex color (do not include the #). Automatically lower-cased.
     *
     * @return this, for chaining.
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    public HexColorValue color(String color) {
        checkNotNull(color, "color cannot be null");

        // remove leading '#' if present
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }

        // color is automatically lower-cased
        this.color = color.toLowerCase();

        return this;
    }

    /**
     * Gets the color value (does not include the #).
     *
     * @return The color value.
     */
    public String color() {
        return color;
    }

    /**
     * Gets whether this hex color is shorthand (has a length of three).
     *
     * @return True if the length of this color is 3.
     */
    public boolean isShorthand() {
        return color.length() == 3;
    }

    /**
     * Gets the color value (does not include the #). Prefer to use {@link #color()}, which is identical to this method.
     *
     * @return The color value.
     */
    @Override
    public String textualValue() {
        return color();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('#').append(color);
    }

    @Override
    public HexColorValue copy() {
        return new HexColorValue(color, true).copiedFrom(this);
    }

    /**
     * Creates a new {@link HexColorValue} instance using the given color (do not include the #).
     * <p>
     * Example:
     * <pre>
     * <code> HexColorValue.of("fffeee")</code>
     * </pre>
     *
     * @param color
     *     The color value. Automatically lower-cased.
     *
     * @return The new {@link HexColorValue} instance.
     */
    public static HexColorValue of(String color) {
        return new HexColorValue(color);
    }
}
