/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.salesforce.omakase.As;

/**
 * TODO Description
 * 
 * <p>
 * we use two integers instead of a double because we want to preserve the information regarding the presence of the
 * decimal point as authored.
 * 
 * @author nmcwilliams
 */
public class NumericalValue implements Term {

    private Integer integerValue;
    private Optional<Integer> decimalValue = Optional.absent();
    private Optional<String> unit = Optional.absent();
    private Optional<Sign> explicitSign = Optional.absent();

    /** TODO */
    public enum Sign {
        /** TODO */
        POSITIVE('+'),
        /** TODO */
        NEGATIVE('_');

        final char symbol;

        Sign(char symbol) {
            this.symbol = symbol;
        }
    }

    /**
     * TODO
     * 
     * @param integerValue
     *            TODO
     */
    public NumericalValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * TODO Description
     * 
     * @param integerValue
     *            TODO
     * @return TODO
     */
    public NumericalValue integerValue(Integer integerValue) {
        this.integerValue = checkNotNull(integerValue, "integerValue cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Integer integerValue() {
        return integerValue;
    }

    /**
     * TODO Description
     * 
     * @param decimalValue
     *            TODO
     * @return TODO
     */
    public NumericalValue decimalValue(Integer decimalValue) {
        this.decimalValue = Optional.fromNullable(decimalValue);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<Integer> decimalValue() {
        return decimalValue;
    }

    /**
     * TODO Description
     * 
     * @param unit
     *            TODO
     * @return TODO
     */
    public NumericalValue unit(String unit) {
        this.unit = Optional.fromNullable(unit);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public Optional<String> unit() {
        return unit;
    }

    /**
     * TODO Description TODO
     * 
     * @param sign
     *            TODO
     * @return TODO
     */
    public NumericalValue explicitSign(Sign sign) {
        this.explicitSign = Optional.fromNullable(sign);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
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
