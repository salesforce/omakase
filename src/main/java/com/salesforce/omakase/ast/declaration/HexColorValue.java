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

package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.data.Prefix;
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
     * <p/>
     * If dynamically creating a new instance then use {@link #HexColorValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param color
     *     The hex color (do not include the #).
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
     * Sets the value of the color (converted to lower-case).
     *
     * @param color
     *     The hex color (do not include the #).
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

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append('#').append(color);
    }

    @Override
    protected HexColorValue makeCopy(Prefix prefix, SupportMatrix support) {
        return new HexColorValue(color, true);
    }

    /**
     * Creates a new {@link HexColorValue} instance using the given color (do not include the #).
     * <p/>
     * Example:
     * <pre>
     * <code> HexColorValue.of("fffeee")</code>
     * </pre>
     *
     * @param color
     *     The color value.
     *
     * @return The new {@link HexColorValue} instance.
     */
    public static HexColorValue of(String color) {
        return new HexColorValue(color);
    }
}
