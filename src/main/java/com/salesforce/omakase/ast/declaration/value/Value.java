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

package com.salesforce.omakase.ast.declaration.value;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Declaration;

import java.util.List;

/**
 * TESTME
 * <p/>
 * Utilities for working with {@link PropertyValue}s and {@link Term}s.
 * <p/>
 * This allows you to extract specific {@link Term} instances from a given {@link PropertyValue} if that {@link Term} is the only
 * member within the list. It also provides utilities for casting a generic {@link PropertyValue} to a more specific one, such as
 * {@link #asTermList(PropertyValue)}.
 * <p/>
 * Examples:
 * <pre>
 * {@code Optional<HexColorValue> color = Value.asHexColor(declaration.getPropertyValue())}
 * {@code Optional<KeywordValue> keyword = Value.asKeyword(declaration.getPropertyValue())}
 * {@code Optional<NumericalValue> number = Value.asNumerical(declaration.getPropertyValue())}
 * {@code Optional<TermList> termList = Value.asTermList(declaration.getPropertyValue())}
 * </pre>
 * <p/>
 * The returned {@link Optional} instances are wrappers around the actual object instance. If the {@link PropertyValue} matches
 * the conditions of the method being called then you will get an  {@link Optional} where {@link Optional#isPresent()} is true.
 * You can get at the object instance via {@link Optional#get()}.
 * <p/>
 * However, if the {@link PropertyValue} does not match the conditions of the method then the returned {@link Optional} will be
 * {@link Optional#absent()}. Thus, you should always check {@link Optional#isPresent()} before attempting to access the inner
 * object instance.
 *
 * @author nmcwilliams
 */
public final class Value {
    /** do not construct */
    private Value() {}

    /**
     * Gets the given value as {@link TermList}.
     * <p/>
     * This checks if the given {@link PropertyValue} is an instance of a {@link TermList}.
     *
     * @param value
     *     The {@link TermList} if the value is an instance of {@link TermList}, otherwise {@link Optional#absent()}.
     *
     * @return The {@link TermList}, or {@link Optional#absent()} if the value is not a {@link TermList}.
     */
    public static Optional<TermList> asTermList(PropertyValue value) {
        return value instanceof TermList ? Optional.of((TermList)value) : Optional.<TermList>absent();
    }

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
