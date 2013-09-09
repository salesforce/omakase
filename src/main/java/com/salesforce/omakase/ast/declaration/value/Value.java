/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Declaration;

import java.util.List;

/**
 * TESTME
 * <p/>
 * Utilities for working with {@link PropertyValue}s and {@link Term}s.
 * <p/>
 * Examples:
 * <pre>
 * {@code HexColorValue color = Value.asHexColor(declaration.getPropertyValue())}
 * {@code KeywordValue keyword = Value.asKeyword(declaration.getPropertyValue())}
 * {@code NumericalValue number = Value.asNumerical(declaration.getPropertyValue())}
 * </pre>
 *
 * @author nmcwilliams
 */
public final class Value {
    /** do not construct */
    private Value() {}

    /**
     * Gets the single {@link HexColorValue} within the given {@link PropertyValue}. The returned {@link Optional} will only be
     * present if the given property value contains only one {@link Term} which is an instance of a {@link HexColorValue}.
     * <p/>
     * This allows you to work with {@link HexColorValue} specific methods when getting a {@link Declaration}'s property value.
     * <p/>
     * Example:
     * <pre>
     * {@code HexColorValue color = Value.asHexColor(declaration.getPropertyValue())}
     * </pre>
     *
     * @param value
     *     The value.
     *
     * @return The hex color value, or {@link Optional#absent()} if the {@link PropertyValue} doesn't match the conditions as
     *         stated above.
     */
    public static Optional<HexColorValue> asHexColor(PropertyValue value) {
        return as(HexColorValue.class, value);
    }

    /**
     * Gets the single {@link KeywordValue} within the given {@link PropertyValue}. The returned {@link Optional} will only be
     * present if the given property value contains only one {@link Term} which is an instance of a {@link KeywordValue}.
     * <p/>
     * This allows you to work with {@link KeywordValue} specific methods when getting a {@link Declaration}'s property value.
     * <p/>
     * Example:
     * <pre>
     * {@code KeywordValue keyword = Value.asKeyword(declaration.getPropertyValue())}
     * </pre>
     *
     * @param value
     *     The value.
     *
     * @return The keyword value, or {@link Optional#absent()} if the {@link PropertyValue} doesn't match the conditions as stated
     *         above.
     */
    public static Optional<KeywordValue> asKeyword(PropertyValue value) {
        return as(KeywordValue.class, value);
    }

    /**
     * Gets the single {@link NumericalValue} within the given {@link PropertyValue}. The returned {@link Optional} will only be
     * present if the given property value contains only one {@link Term} which is an instance of a {@link NumericalValue}.
     * <p/>
     * This allows you to work with {@link NumericalValue} specific methods when getting a {@link Declaration}'s property value.
     * <p/>
     * Example:
     * <pre>
     * {@code NumericalValue number = Value.asNumerical(declaration.getPropertyValue())}
     * </pre>
     *
     * @param value
     *     The value.
     *
     * @return The numerical value, or {@link Optional#absent()} if the {@link PropertyValue} doesn't match the conditions as
     *         stated above.
     */
    public static Optional<NumericalValue> asNumerical(PropertyValue value) {
        return as(NumericalValue.class, value);
    }

    /**
     * Gets the single {@link StringValue} within the given {@link PropertyValue}. The returned {@link Optional} will only be
     * present if the given property value contains only one {@link Term} which is an instance of a {@link StringValue}.
     * <p/>
     * This allows you to work with {@link StringValue} specific methods when getting a {@link Declaration}'s property value.
     * <p/>
     * Example:
     * <pre>
     * {@code StringValue string = Value.asString(declaration.getPropertyValue())}
     * </pre>
     *
     * @param value
     *     The value.
     *
     * @return The string value, or {@link Optional#absent()} if the {@link PropertyValue} doesn't match the conditions as stated
     *         above.
     */
    public static Optional<StringValue> asString(PropertyValue value) {
        return as(StringValue.class, value);
    }

    /**
     * Helper method to convert or extract the more narrowly-typed {@link Term} instance.
     *
     * @param <T>
     *     The type of {@link Term}.
     * @param klass
     *     Class of the {@link Term}.
     * @param value
     *     The {@link PropertyValue} that is or contains the {@link Term}.
     *
     * @return the properly-typed instance, or {@link Optional#absent()} if it doesn't match.
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
     * Helper method to extract a {@link Term} instance from a {@link TermList} if and only if it is the only {@link Term} in the
     * list.
     *
     * @param <T>
     *     Type of the {@link Term} to extract.
     * @param klass
     *     Class of the {@link Term} to extract.
     * @param termList
     *     Extract the {@link Term} from this {@link TermList}.
     *
     * @return The extracted {@link Term}, or {@link Optional#absent()} if it doesn't match the conditions stated above.
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
