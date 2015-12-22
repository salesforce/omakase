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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.*;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A numerical value (e.g., 1 or 1px or 3.5em).
 * <p/>
 * The unit is optional and is any keyword directly following the number value, such as px, em, or ms. The sign is also optional,
 * and is only defined if explicitly included in the source. In other words, in "5px" the sign will <b>not</b> be {@link
 * Sign#POSITIVE} but {@link Optional#absent()}.
 * <p/>
 * To dynamically create a {@link NumericalValue} use on of the constructor methods, for example:
 * <pre>
 * <code>NumericalValue number = NumericalValue.of(10, "px");</code>
 * </pre>
 *
 * @author nmcwilliams
 * @see NumericalValueParser
 */
@Subscribable
@Description(value = "individual numerical value", broadcasted = REFINED_DECLARATION)
public final class NumericalValue extends AbstractTerm {
    private String raw;
    private Optional<String> unit = Optional.absent();
    private Optional<Sign> explicitSign = Optional.absent();

    /** Represents the sign of the number (+/-) */
    public enum Sign {
        /** plus sign */
        POSITIVE('+'),
        /** minus sign */
        NEGATIVE('-');

        final char symbol;

        Sign(char symbol) { this.symbol = symbol; }
    }

    /**
     * Constructs a new {@link NumericalValue} instance with the given raw value.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param raw
     *     The number. Must not include the sign or the unit.
     */
    public NumericalValue(int line, int column, String raw) {
        super(line, column);
        this.raw = raw;
    }

    /**
     * Constructs a new {@link NumericalValue} instance (used for dynamically created {@link Syntax} units).
     *
     * @param value
     *     The numerical value.
     */
    public NumericalValue(int value) {
        value(value);
    }

    /**
     * Constructs a new {@link NumericalValue} instance (used for dynamically created {@link Syntax} units).
     * <p/>
     * Please note that large integer or floating-point values may result in unexpected precision and/or rounding errors.
     *
     * @param value
     *     The numerical value.
     */
    public NumericalValue(double value) {
        value(value);
    }

    /**
     * Sets the numerical value.
     *
     * @param value
     *     The new numerical value.
     *
     * @return this, for chaining.
     */
    public NumericalValue value(int value) {
        this.raw = Integer.toString(Math.abs(value));
        if (value < 0) {
            explicitSign = Optional.of(Sign.NEGATIVE);
        } else {
            explicitSign = Optional.absent();
        }
        return this;
    }

    /**
     * Sets the numerical value with a decimal point.
     * <p/>
     * Please note that large integer or floating-point values may result in unexpected precision and/or rounding errors.
     *
     * @param value
     *     The new numerical value.
     *
     * @return this, for chaining.
     */
    public NumericalValue value(double value) {
        DecimalFormat fmt = new DecimalFormat("#");
        fmt.setMaximumIntegerDigits(309);
        fmt.setMinimumIntegerDigits(1);
        fmt.setMaximumFractionDigits(340);
        this.raw = fmt.format(Math.abs(value));
        if (value < 0) {
            explicitSign = Optional.of(Sign.NEGATIVE);
        } else {
            explicitSign = Optional.absent();
        }
        return this;
    }

    /**
     * Gets the numerical value as a string.
     * <p/>
     * Note that if the value is negative, it will not be shown here, see {@link #explicitSign()}, or for mathematical
     * operations see {@link #intValue()} or {@link #doubleValue()}.
     * <p/>
     * For math operations, {@link #doubleValue()} is available instead.
     *
     * @return The numerical value.
     */
    public String value() {
        return raw;
    }

    /**
     * Gets the numerical value as a double.
     * <p/>
     * Note that this may result in an exception if the current string value is too large for a double. This performs a
     * calculation so cache the result if using more than once.
     *
     * @return The double value.
     */
    public double doubleValue() {
        double d = Double.parseDouble(raw);
        return isNegative() ? d * -1 : d;
    }

    /**
     * Gets the numerical value as an integer. Usually you should use {@link #doubleValue()} instead unless you are ok with
     * discarding any present floating point value. This performs a calculation so cache the result if using more than once.
     *
     * @return The int value.
     */
    public int intValue() {
        int i = Integer.parseInt(raw);
        return isNegative() ? i * -1 : i;
    }

    /**
     * Sets the unit, e.g., px or em.
     *
     * @param unit
     *     The unit.
     *
     * @return this, for chaining.
     */
    public NumericalValue unit(String unit) {
        this.unit = Optional.fromNullable(unit);
        return this;
    }

    /**
     * Gets the unit.
     *
     * @return The unit, or {@link Optional#absent()} if not set.
     */
    public Optional<String> unit() {
        return unit;
    }

    /**
     * Sets the explicit sign of the number.
     *
     * @param sign
     *     The sign.
     *
     * @return this, for chaining.
     */
    public NumericalValue explicitSign(Sign sign) {
        this.explicitSign = Optional.fromNullable(sign);
        return this;
    }

    /**
     * Gets the explicit sign of the number.
     *
     * @return The sign, or {@link Optional#absent()} if not set.
     */
    public Optional<Sign> explicitSign() {
        return explicitSign;
    }

    /**
     * Gets whether a sign is present and if the sign is negative.
     *
     * @return True if a sign is present and the sign is negative, false otherwise.
     */
    public boolean isNegative() {
        return explicitSign.isPresent() && explicitSign.get() == Sign.NEGATIVE;
    }

    /**
     * Returns the sign (if explicitly present), the number, and the unit (if present).
     *
     * @return The number, including sign and and unit if explicitly present.
     */
    @Override
    public String textualValue() {
        StringBuilder builder = new StringBuilder();

        if (explicitSign.isPresent()) {
            builder.append(explicitSign.get().symbol);
        }

        builder.append(raw);

        if (unit.isPresent()) {
            builder.append(unit.get());
        }

        return builder.toString();
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        // sign
        if (explicitSign.isPresent()) {
            appendable.append(explicitSign.get().symbol);
        }

        // number. omit leading 0 integer values when there is only a decimal, e.g., "0.5" => ".5"
        boolean leadingZero = raw.length() > 2 && raw.charAt(0) == '0' && raw.charAt(1) == '.';
        appendable.append(leadingZero ? raw.substring(1) : raw);

        // unit
        if (unit.isPresent()) {
            appendable.append(unit.get());
        }
    }

    @Override
    public NumericalValue copy() {
        NumericalValue copy = new NumericalValue(-1, -1, raw).copiedFrom(this);
        if (unit.isPresent()) copy.unit(unit.get());
        if (explicitSign.isPresent()) copy.explicitSign(explicitSign.get());
        return copy;
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given integer value.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of(10)</code>
     * </pre>
     *
     * @param value
     *     The value.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(int value) {
        return new NumericalValue(value);
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given double value.
     * <p/>
     * Please note that large integer or floating-point values may result in unexpected precision and/or rounding errors.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of(10.5)</code>
     * </pre>
     *
     * @param doubleValue
     *     The floating-point value.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(double doubleValue) {
        return new NumericalValue(doubleValue);
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given raw value. Generally this should not be preferred unless using
     * one of the other constructor methods would result in precision errors.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of("1000000000.0000000009")</code>
     * </pre>
     *
     * @param value
     *     The raw numerical value. Must not contain the sign or unit.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(String value) {
        checkArgument(!value.startsWith("-"), "to set the sign, use #explicitSign instead");
        return new NumericalValue(-1, -1, checkNotNull(value));
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given integer value and unit.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of(10, "px")</code>
     * </pre>
     *
     * @param integerValue
     *     The integer value.
     * @param unit
     *     The unit, e.g., px or em.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(int integerValue, String unit) {
        return of(integerValue).unit(unit);
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given double value and unit.
     * <p/>
     * Please note that large integer or floating-point values may result in unexpected precision and/or rounding errors.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of(10.5, "px")</code>
     * </pre>
     *
     * @param doubleValue
     *     The floating-point value.
     * @param unit
     *     The unit, e.g., px or em.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(double doubleValue, String unit) {
        return of(doubleValue).unit(unit);
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given raw value. Generally this should not be preferred unless using
     * one of the other constructor methods would result in precision errors.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of("1000000000.0000000009", "px")</code>
     * </pre>
     *
     * @param raw
     *     The raw numerical value. Must not contain the sign or unit.
     * @param unit
     *     The unit, e.g., px or em.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(String raw, String unit) {
        return of(raw).unit(unit);
    }
}
