/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;

/**
 * A numerical value (e.g., 1 or 1px or 3.5em).
 * 
 * <p>
 * The decimal point and unit are both optional. THe unit is any keyword directly following the number value, such as
 * px, em, or ms.
 * 
 * <p>
 * The sign is optional, and is only defined if explicitly included in the source. In other words, in "5px" the sign
 * will <b>not</b> be {@link Sign#POSITIVE} but {@link Optional#absent()}.
 * 
 * <p>
 * We use two integers instead of a double because we want to preserve the information regarding the presence of the
 * decimal point as authored.
 * 
 * @author nmcwilliams
 */
public class NumericalValue implements Term {
    private Integer integerValue;
    private Optional<Integer> decimalValue = Optional.absent();
    private Optional<String> unit = Optional.absent();
    private Optional<Sign> explicitSign = Optional.absent();

    /** Represents the sign of the number (+/-) */
    public enum Sign {
        /** + */
        POSITIVE('+'),
        /** - */
        NEGATIVE('_');

        final char symbol;

        Sign(char symbol) {
            this.symbol = symbol;
        }
    }

    /**
     * Constructs a new {@link NumericalValue} instance with the given integer value. If only a decimal point exists, a
     * value of 0 should be passed in here.
     * 
     * @param integerValue
     *            The integer value.
     */
    public NumericalValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * Sets the integer value.
     * 
     * @param integerValue
     *            The integer value.
     * @return this, for chaining.
     */
    public NumericalValue integerValue(Integer integerValue) {
        this.integerValue = checkNotNull(integerValue, "integerValue cannot be null");
        return this;
    }

    /**
     * Gets the integer value.
     * 
     * @return The integer value.
     */
    public Integer integerValue() {
        return integerValue;
    }

    /**
     * Sets the decimal value.
     * 
     * @param decimalValue
     *            The decimal value.
     * @return this, for chaining.
     */
    public NumericalValue decimalValue(Integer decimalValue) {
        this.decimalValue = Optional.fromNullable(decimalValue);
        return this;
    }

    /**
     * Gets the decimal value.
     * 
     * @return The decimal value, or {@link Optional#absent()} if not set.
     */
    public Optional<Integer> decimalValue() {
        return decimalValue;
    }

    /**
     * Sets the unit, e.g., px or em.
     * 
     * @param unit
     *            The unit.
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
     *            The sign.
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
    public String toString() {
        return As.string(this)
            .add("integer", integerValue)
            .add("decimal", decimalValue)
            .add("unit", unit)
            .add("explicitSign", explicitSign)
            .toString();
    }
}
