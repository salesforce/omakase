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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.*;

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
public class PropertyName extends AbstractSyntax {
    /** pattern for the vendor prefix */
    private static final char STAR = '*';

    private final String name;
    private Optional<String> prefix;
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
            int end = name.indexOf("-", 1);
            this.prefix = Optional.of(name.substring(0, end + 1));
            this.name = name.substring(end + 1);
        } else {
            prefix = Optional.absent();
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
     * Gets the prefix, if present.
     *
     * @return The prefix, or {@link Optional#absent()} if no prefix exists.
     */
    public Optional<String> prefix() {
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
        return prefix(prefix.toString());
    }

    /**
     * Sets the prefix for this property name. Prefer to use {@link #prefix(Prefix)} instead.
     *
     * @param prefix
     *     The prefix, including both dashes, e.g., "-webkit-".
     *
     * @return this, for chaining.
     */
    public PropertyName prefix(String prefix) {
        checkNotNull(prefix, "prefix cannot be null (use #removePrefix instead)");
        checkArgument(prefix.startsWith("-"), "prefixes must start with a dash");
        checkArgument(prefix.endsWith("-"), "prefixes must end with a dash");
        this.prefix = Optional.of(prefix);
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

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (starHack) {
            appendable.append(STAR);
        }
        appendable.append(name());
    }

    @Override
    public String toString() {
        return As.string(this).add("name", name()).toString();
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
        Property recognized = Property.map.get(name.toLowerCase());
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
     * Gets whether this {@link PropertyName} includes an IE7 <a href="http://en.wikipedia.org/wiki/CSS_filter#Star_hack">star
     * hack.</a>
     *
     * @return True if this {@link PropertyName} includes the IE7 star hack.
     */
    public boolean hasStarHack() {
        return starHack;
    }

    /**
     * Sets if this {@link PropertyName} includes an IE7 <a href="http://en.wikipedia.org/wiki/CSS_filter#Star_hack">star
     * hack.</a>
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

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PropertyName) {
            final PropertyName that = (PropertyName)other;
            return name().equals(that.name());
        }

        return false;
    }
}
