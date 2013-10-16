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
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_DECLARATION;

/**
 * A numerical value (e.g., 1 or 1px or 3.5em).
 * <p/>
 * The decimal point and unit are both optional. THe unit is any keyword directly following the number value, such as px, em, or
 * ms.
 * <p/>
 * The sign is optional, and is only defined if explicitly included in the source. In other words, in "5px" the sign will
 * <b>not</b> be {@link Sign#POSITIVE} but {@link Optional#absent()}.
 * <p/>
 * We use two integers instead of a double because we want to preserve the information regarding the presence of the decimal point
 * as authored.
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
    private Long integerValue;
    private Optional<Long> decimalValue = Optional.absent();
    private Optional<String> unit = Optional.absent();
    private Optional<Sign> explicitSign = Optional.absent();

    /** Represents the sign of the number (+/-) */
    public enum Sign {
        /** plus sign */
        POSITIVE('+'),
        /** minus sign */
        NEGATIVE('-');

        final char symbol;

        Sign(char symbol) {
            this.symbol = symbol;
        }
    }

    /**
     * Constructs a new {@link NumericalValue} instance with the given integer value. If only a decimal point exists, a value of 0
     * should be passed in here.
     * <p/>
     * If dynamically creating a new instance then use {@link #NumericalValue(int)} or {@link #NumericalValue(long)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param integerValue
     *     The integer value.
     */
    public NumericalValue(int line, int column, Long integerValue) {
        super(line, column);
        this.integerValue = integerValue;
    }

    /**
     * Constructs a new {@link NumericalValue} instance with the given integer value. If only a decimal point exists, a value of 0
     * should be passed in here.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param integerValue
     *     The integer value.
     */
    public NumericalValue(int line, int column, Integer integerValue) {
        super(line, column);
        this.integerValue = (long)integerValue;
    }

    /**
     * Constructs a new {@link NumericalValue} instance (used for dynamically created {@link Syntax} units).
     *
     * @param integerValue
     *     The integer value.
     */
    public NumericalValue(long integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * Constructs a new {@link NumericalValue} instance (used for dynamically created {@link Syntax} units).
     *
     * @param integerValue
     *     The integer value.
     */
    public NumericalValue(int integerValue) {
        this.integerValue = (long)integerValue;
    }

    /**
     * Sets the integer value.
     *
     * @param integerValue
     *     The integer value.
     *
     * @return this, for chaining.
     */
    public NumericalValue integerValue(int integerValue) {
        return integerValue((long)integerValue);
    }

    /**
     * Sets the integer value.
     *
     * @param integerValue
     *     The integer value.
     *
     * @return this, for chaining.
     */
    public NumericalValue integerValue(long integerValue) {
        checkArgument(integerValue >= 0, "integerValue must be greater than 0 (use #explicitSign for negative values)");
        this.integerValue = integerValue;
        return this;
    }

    /**
     * Gets the integer value.
     *
     * @return The integer value.
     */
    public Long integerValue() {
        return integerValue;
    }

    /**
     * Sets the decimal value.
     *
     * @param decimalValue
     *     The decimal value.
     *
     * @return this, for chaining.
     */
    public NumericalValue decimalValue(long decimalValue) {
        checkArgument(integerValue >= 0, "decimalValue must be greater than 0");
        this.decimalValue = Optional.of(decimalValue);
        return this;
    }

    /**
     * Gets the decimal value.
     *
     * @return The decimal value, or {@link Optional#absent()} if not set.
     */
    public Optional<Long> decimalValue() {
        return decimalValue;
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

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (explicitSign.isPresent()) {
            appendable.append(explicitSign.get().symbol);
        }
        if (!decimalValue.isPresent() || integerValue.intValue() != 0) {
            appendable.append(integerValue.toString());
        }
        if (decimalValue.isPresent()) {
            appendable.append('.');
            appendable.append(decimalValue.get().toString());
        }
        if (unit.isPresent()) {
            appendable.append(unit.get());
        }
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("integer", integerValue)
            .add("decimal", decimalValue)
            .add("unit", unit)
            .add("explicitSign", explicitSign)
            .addUnlessEmpty("comments", comments())
            .toString();
    }

    /**
     * Creates a new {@link NumericalValue} instance with the given integer value.
     * <p/>
     * Example:
     * <pre>
     * <code>NumericalValue.of(10)</code>
     * </pre>
     *
     * @param integerValue
     *     The integer value. If only a decimal point exists, a value of 0 should be passed in here.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(int integerValue) {
        return new NumericalValue(integerValue);
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
     *     he integer value. If only a decimal point exists, a value of 0 should be passed in here.
     * @param unit
     *     The unit, e.g., px or em.
     *
     * @return The new {@link NumericalValue} instance.
     */
    public static NumericalValue of(int integerValue, String unit) {
        return new NumericalValue(integerValue).unit(unit);
    }
}
