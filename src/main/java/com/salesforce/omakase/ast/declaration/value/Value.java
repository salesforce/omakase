/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import java.util.List;

import com.google.common.base.Optional;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Value {
    /** do not construct */
    private Value() {}

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    public static Optional<HexColorValue> asHexColor(PropertyValue value) {
        return as(HexColorValue.class, value);
    }

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    public static Optional<KeywordValue> asKeyword(PropertyValue value) {
        return as(KeywordValue.class, value);
    }

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    public static Optional<NumericalValue> asNumerical(PropertyValue value) {
        return as(NumericalValue.class, value);
    }

    /**
     * TODO Description
     * 
     * @param value
     *            TODO
     * @return TODO
     */
    public static Optional<StringValue> asString(PropertyValue value) {
        return as(StringValue.class, value);
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @param value
     *            TODO
     * @return TODO
     */
    private static <T extends TermListMember> Optional<T> as(Class<T> klass, PropertyValue value) {
        if (klass.isInstance(value)) {
            return Optional.of(klass.cast(value));
        } else if (value instanceof TermList) {
            return checkTermList(klass, (TermList)value);
        } else {
            return Optional.absent();
        }
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param klass
     *            TODO
     * @param termList
     *            TODO
     * @return TODO
     */
    private static <T extends TermListMember> Optional<T> checkTermList(Class<T> klass, TermList termList) {
        List<TermListMember> terms = termList.members();
        if (terms.size() == 1) {
            TermListMember member = terms.get(0);
            if (klass.isAssignableFrom(member.getClass())) return Optional.of(klass.cast(member));
        }
        return Optional.absent();
    }
}
