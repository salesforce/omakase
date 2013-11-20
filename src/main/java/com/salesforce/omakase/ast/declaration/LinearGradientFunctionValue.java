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

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.util.As;
import com.salesforce.omakase.util.Copy;
import com.salesforce.omakase.util.Parsers;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a linear-gradient (or repeating-linear-gradient) function.
 * <p/>
 * Use {@link #repeating(boolean)} if the gradient is a repeating-linear-gradient.
 *
 * @author nmcwilliams
 */
public final class LinearGradientFunctionValue extends AbstractTerm implements FunctionValue {
    private final Map<String, String> DIR_FLIP = ImmutableMap.<String, String>builder()
        .put("to bottom", "top")
        .put("to top", "bottom")
        .put("to right", "left")
        .put("to left", "right")
        .put("to bottom right", "top left")
        .put("to bottom left", "top right")
        .put("to top right", "bottom left")
        .put("to top left", "bottom right")
        .build();

    private String args;
    private boolean repeating;

    /**
     * Constructs a new instance of a {@link LinearGradientFunctionValue}.
     * <p/>
     * If dynamically creating a new instance then use {@link #LinearGradientFunctionValue(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param args
     *     The raw function args.
     */
    public LinearGradientFunctionValue(int line, int column, String args) {
        super(line, column);
        this.args = args;
    }

    /**
     * Constructs a new instance of a {@link LinearGradientFunctionValue} (used for dynamically created {@link Syntax} units).
     *
     * @param args
     *     The raw function args.
     */
    public LinearGradientFunctionValue(String args) {
        args(args);
    }

    /**
     * Sets the function arguments.
     *
     * @param args
     *     The raw function args.
     *
     * @return this, for chaining.
     */
    public LinearGradientFunctionValue args(String args) {
        this.args = checkNotNull(args, "args cannot be null");
        return this;
    }

    /**
     * Gets the raw function arguments.
     *
     * @return The function arguments.
     */
    public String args() {
        return args;
    }

    /**
     * Sets whether this linear-gradient is a repeating-linear-gradient.
     *
     * @param repeating
     *     Specify true to indicate this is a repeating-linear-gradient.
     *
     * @return this, for chaining.
     */
    public LinearGradientFunctionValue repeating(boolean repeating) {
        this.repeating = repeating;
        return this;
    }

    /**
     * Gets whether this is a repeating-linear-gradient.
     *
     * @return True if this is a repeating-linear-gradient.
     */
    public boolean repeating() {
        return repeating;
    }

    @Override
    public String name() {
        return repeating ? "repeating-linear-gradient" : "linear-gradient";
    }

    @Override
    public LinearGradientFunctionValue copy() {
        return Copy.comments(this, new LinearGradientFunctionValue(args).repeating(repeating));
    }

    @Override
    public FunctionValue copyWithPrefix(Prefix prefix, SupportMatrix support) {
        if (!support.requiresPrefixForFunction(prefix, name())) return copy();

        String newArgs = args;

        char first = args.charAt(0);
        if (first == 't') {
            // "to" syntax -> "from" syntax
            List<String> split = Lists.newArrayList(Splitter.on(",").limit(2).split(args));
            String from = DIR_FLIP.get(split.get(0));
            if (from != null) {
                newArgs = from + "," + split.get(1);
            }
        } else if (Tokens.DIGIT.matches(first) || first == '-') {
            // convert angle http://www.sitepoint.com/using-unprefixed-css3-gradients-in-modern-browsers/
            Source source = new Source(args);
            Optional<NumericalValue> numerical = Parsers.parseNumerical(source);
            if (numerical.isPresent() && numerical.get().unit().isPresent()) {
                int angle = Math.abs(numerical.get().intValue() - 450) % 360;
                newArgs = angle + numerical.get().unit().get() + source.remaining();
            }
        }

        return Copy.comments(this, new GenericFunctionValue(prefix + name(), newArgs));
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name()).append('(').append(args).append(')');
    }

    @Override
    public String toString() {
        return As.string(this).add("name", name()).add("args", args).toString();
    }
}
