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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.util.Prefixes;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.util.Prefixes.PrefixPair;

/**
 * The property name within a {@link Declaration}.
 * <p/>
 * This class is vendor prefix aware, which means that you can check if it {@link #isPrefixed()}, get the {@link
 * #unprefixedName()}, or add/replace the prefix with {@link #prefix(Prefix)} method.
 * <p/>
 * Use {@link #name()} to get the full property name, including the prefix if it is present.
 *
 * @author nmcwilliams
 */
public final class PropertyName extends AbstractSyntax<PropertyName> {
    private static final char STAR = '*';

    private final String name;
    private Optional<Prefix> prefix;
    private boolean starHack;

    /** private -- use a constructor method for new instances */
    @SuppressWarnings("AssignmentToMethodParameter")
    private PropertyName(int line, int column, String name) {
        super(line, column);

        // the IE7 "star hack" is not part of the CSS syntax, but it still needs to be handled
        if (name.charAt(0) == STAR) {
            setStarHack(true);
            name = name.substring(1);
        }

        if (name.charAt(0) == '-') {
            PrefixPair pair = Prefixes.splitPrefix(name);
            this.prefix = pair.prefix();
            this.name = pair.unprefixed();
        } else {
            this.prefix = Optional.absent();
            this.name = name;
        }
    }

    /**
     * Gets the full property name, including the prefix if present.
     *
     * @return The full property name.
     */
    public String name() {
        return prefix.isPresent() ? prefix.get() + name : name;
    }

    /**
     * Gets the unprefixed property name.
     *
     * @return The unprefixed property name.
     */
    public String unprefixedName() {
        return name;
    }

    /**
     * Gets whether this property name is prefixed.
     *
     * @return True if this property name is prefixed.
     */
    public boolean isPrefixed() {
        return prefix.isPresent();
    }

    /**
     * Gets whether this {@link PropertyName} has the given {@link Prefix}.
     *
     * @param prefix
     *     Match against this prefix.
     *
     * @return True if this {@link PropertyName} has the given {@link Prefix}.
     */
    public boolean hasPrefix(Prefix prefix) {
        return this.prefix.isPresent() && this.prefix.get() == prefix;
    }

    /**
     * Gets the prefix, if present.
     *
     * @return The prefix, or {@link Optional#absent()} if no prefix exists.
     */
    public Optional<Prefix> prefix() {
        return prefix;
    }

    /**
     * Sets the prefix for this property name. This will overwrite any currently specified prefix.
     *
     * @param prefix
     *     The {@link Prefix}.
     *
     * @return this, for chaining.
     */
    public PropertyName prefix(Prefix prefix) {
        this.prefix = Optional.fromNullable(prefix);
        return this;
    }

    /**
     * Removes the current prefix from this property name.
     *
     * @return this, for chaining.
     */
    public PropertyName removePrefix() {
        prefix = Optional.absent();
        return this;
    }

    /**
     * Gets the exact matching {@link Property} instance, if one exists (it may not exist if this is an unknown property or a
     * prefixed property).
     *
     * @return The {@link Property}, or {@link Optional#absent()} if this {@link PropertyName} is prefixed or the property name is
     *         unknown.
     */
    public Optional<Property> asProperty() {
        return isPrefixed() ? Optional.<Property>absent() : Optional.fromNullable(Property.lookup(name));
    }

    /**
     *
     * todo - change
     *
     * Gets the exact matching {@link Property} instance, if one exists (it may not exist if this is an unknown property.)
     *
     * @return The {@link Property}, or {@link Optional#absent()} if this {@link PropertyName} is prefixed or the property name is
     *         unknown.
     */
    public Optional<Property> asPropertyIgnorePrefix() {
        return Optional.fromNullable(Property.lookup(name));
    }



    /**
     * Gets whether this {@link PropertyName} includes an IE7 star hack (http://en.wikipedia.org/wiki/CSS_filter#Star_hack).
     *
     * @return True if this {@link PropertyName} includes the IE7 star hack.
     */
    public boolean hasStarHack() {
        return starHack;
    }

    /**
     * Sets if this {@link PropertyName} includes an IE7 star hack (http://en.wikipedia.org/wiki/CSS_filter#Star_hack).
     *
     * @param starHack
     *     True if this property name includes the star hack
     *
     * @return this, for chaining.
     */
    public PropertyName setStarHack(boolean starHack) {
        this.starHack = starHack;
        return this;
    }

    /**
     * Gets whether this {@link PropertyName} has a {@link #name()} that equals the given string.
     *
     * @param string
     *     Match against this property name.
     *
     * @return True if this {@link PropertyName} has a name that equals the given string.
     */
    public boolean matches(String string) {
        if (string == null) return false;
        return name().equals(string);
    }

    /**
     * Gets whether this {@link PropertyName} has a {@link #name()} that equals the given {@link Property}.
     *
     * @param property
     *     Match against this property.
     *
     * @return True if this {@link PropertyName} has a name that equals the given {@link Property}.
     */
    public boolean matches(Property property) {
        if (property == null) return false;
        return name().equals(property.toString());
    }

    /**
     * Gets whether this {@link PropertyName} has a {@link #name()} that equals the name of the given {@link PropertyName}.
     *
     * @param other
     *     Match against this property name (including the prefix).
     *
     * @return True if the names (including the prefix) are equal.
     */
    public boolean matches(PropertyName other) {
        return name().equals(other.name());
    }

    /**
     * Same as {@link #matches(Property)}, except this ignores the prefix.
     *
     * @param property
     *     Match against this property.
     *
     * @return True if this {@link PropertyName} has a name that equals the given {@link Property}, ignoring the prefix.
     */
    public boolean matchesIgnorePrefix(Property property) {
        return name.equals(property.toString());
    }

    /**
     * Same as {@link #matches(PropertyName)}, except this ignores the prefix.
     *
     * @param other
     *     Match against this property name.
     *
     * @return True if both {@link PropertyName}s have equal {@link #unprefixedName()}s.
     */
    public boolean matchesIgnorePrefix(PropertyName other) {
        return unprefixedName().equals(other.unprefixedName());
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (starHack) appendable.append(STAR);
        appendable.append(name());
    }

    @Override
    protected PropertyName makeCopy(Prefix prefix, SupportMatrix support) {
        if (prefix != null && support != null) {
            Optional<Property> property = asProperty();
            if (property.isPresent() && support.requiresPrefixForProperty(prefix, property.get())) {
                return PropertyName.using(property.get()).prefix(prefix).setStarHack(starHack);
            }
        }
        return PropertyName.using(name()).setStarHack(starHack);
    }

    /**
     * Creates a new {@link PropertyName} instance using the given string. Prefer to use {@link #using(Property)} instead.
     * <p/>
     * Please note that the property name will be automatically lower-cased.
     *
     * @param name
     *     The property name.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName using(String name) {
        checkNotNull(name, "name cannot be null");
        return using(-1, -1, name);
    }

    /**
     * Creates a new {@link PropertyName} from with the given String name. Prefer to use {@link #using(Property)} instead.
     * <p/>
     * Please note that the property name will be automatically lower-cased.
     *
     * @param name
     *     The name of the property.
     * @param line
     *     The line number of the start of the property name.
     * @param column
     *     The column number of the start of the property.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName using(int line, int column, String name) {
        Property recognized = Property.lookup(name.toLowerCase());
        String nameToUse = recognized != null ? recognized.toString() : name.toLowerCase();
        return new PropertyName(line, column, nameToUse);
    }

    /**
     * Creates a new {@link PropertyName} instance from the given {@link Property}.
     *
     * @param property
     *     The {@link Property} name.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName using(Property property) {
        return using(-1, -1, property);
    }

    /**
     * Creates a new {@link PropertyName} instance from the given {@link Property}, line, and column numbers.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param property
     *     The {@link Property}.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName using(int line, int column, Property property) {
        checkNotNull(property, "property cannot be null");
        return new PropertyName(line, column, property.toString());
    }
}
