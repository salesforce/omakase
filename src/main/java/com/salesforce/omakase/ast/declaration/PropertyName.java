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
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Named;
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
 * This class is vendor prefix aware, which means that you can check if it {@link #isPrefixed()}, get the {@link #unprefixed()},
 * or add/replace the prefix with {@link #prefix(Prefix)} method.
 * <p/>
 * Use {@link #name()} to get the full property name, including the prefix if it is present.
 * <p/>
 * The name of this property is immutable. If seeking to change the property name of a Declaration, see {@link
 * Declaration#propertyName(Property)}, {@link Declaration#propertyName(String)}, and related methods (but this is generally
 * discouraged, as many plugins check and depend on the property name-- in some cases it might be better to replace the whole
 * declaration).
 *
 * @author nmcwilliams
 */
public final class PropertyName extends AbstractSyntax implements Named {
    private static final char STAR = '*';
    private static final char PREFIX_START = '-';

    private Prefix prefix;
    private boolean starHack;

    private final Property cached;
    private final String unprefixed;

    /** private -- use a constructor method for new instances */
    @SuppressWarnings("AssignmentToMethodParameter")
    private PropertyName(int line, int column, String name) {
        super(line, column);

        name = name.toLowerCase(); // for output consistency and Property enum lookup

        // the IE7 "star hack" is not part of the CSS syntax, but it still needs to be handled
        if (name.charAt(0) == STAR) {
            starHack(true);
            name = name.substring(1);
        }

        if (name.charAt(0) == PREFIX_START) {
            PrefixPair pair = Prefixes.splitPrefix(name);
            this.prefix = pair.prefix().orNull();
            this.unprefixed = pair.unprefixed();
            this.cached = Property.lookup(pair.unprefixed());
        } else {
            this.prefix = null;
            this.unprefixed = name;
            this.cached = Property.lookup(name);
        }
    }

    /** private -- use a constructor method for new instances */
    private PropertyName(int line, int column, Property property) {
        super(line, column);

        this.prefix = null;
        this.cached = property;
        this.unprefixed = property.toString();
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
    public PropertyName starHack(boolean starHack) {
        this.starHack = starHack;
        return this;
    }

    /**
     * Gets the full property name, including the prefix if present.
     *
     * @return The full property name.
     */
    @Override
    public String name() {
        return prefix != null ? prefix + unprefixed : unprefixed;
    }

    /**
     * Gets the unprefixed property name.
     *
     * @return The unprefixed property name.
     */
    public String unprefixed() {
        return unprefixed;
    }

    /**
     * Gets whether this property name is prefixed.
     *
     * @return True if this property name is prefixed.
     */
    public boolean isPrefixed() {
        return prefix != null;
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
        return this.prefix != null && this.prefix == prefix;
    }

    /**
     * Gets the prefix, if present.
     *
     * @return The prefix, or {@link Optional#absent()} if no prefix exists.
     */
    public Optional<Prefix> prefix() {
        return Optional.fromNullable(prefix);
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
        this.prefix = prefix;
        return this;
    }

    /**
     * Removes the current prefix from this property name.
     *
     * @return this, for chaining.
     */
    public PropertyName removePrefix() {
        prefix = null;
        return this;
    }

    /**
     * Gets the exact matching {@link Property} instance, if one exists (it may not exist if this is an unknown property or a
     * prefixed property).
     *
     * @return The {@link Property}, or {@link Optional#absent()} if this {@link PropertyName} is prefixed or it's unknown.
     */
    public Optional<Property> asProperty() {
        return prefix == null ? Optional.fromNullable(cached) : Optional.<Property>absent();
    }

    /**
     * Gets matching {@link Property} instance, if one exists (it may not exist if this is an unknown property.) This ignores the
     * prefix.
     *
     * @return The {@link Property}, or {@link Optional#absent()} if this {@link PropertyName} is unknown.
     */
    public Optional<Property> asPropertyIgnorePrefix() {
        return Optional.fromNullable(cached);
    }

    /**
     * Gets whether this {@link PropertyName} has a {@link #name()} that equals the given string.
     *
     * @param name
     *     Match against this property name.
     *
     * @return True if this {@link PropertyName} has a name that equals the given string, including the prefix if present.
     */
    public boolean matches(String name) {
        return name != null && name().equals(name);
    }

    /**
     * Gets whether this {@link PropertyName} has a {@link #name()} that equals the given {@link Property}. If this {@link
     * PropertyName} is prefixed then this will always return false.
     *
     * @param property
     *     Match against this property.
     *
     * @return True if this {@link PropertyName} has a name that equals the given {@link Property}.
     */
    public boolean matches(Property property) {
        return property != null && prefix == null && property == cached;
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
        return property != null && property == cached;
    }

    /**
     * Same as {@link #matches(PropertyName)}, except this ignores the prefix.
     *
     * @param other
     *     Match against this property name.
     *
     * @return True if both {@link PropertyName}s have equal {@link #unprefixed()}s.
     */
    public boolean matchesIgnorePrefix(PropertyName other) {
        return unprefixed().equals(other.unprefixed());
    }

    /**
     * Same as {@link #matches(String)}, except this ignores the prefix of this property.
     *
     * @param name
     *     The property name.
     *
     * @return True if this {@link PropertyName} has a name that equals the given string, ignoring the prefix of this property.
     */
    public boolean matchesIgnorePrefix(String name) {
        return unprefixed().equals(name);
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (starHack) appendable.append(STAR);
        appendable.append(name());
    }

    @Override
    public PropertyName copy() {
        if (cached != null) {
            return PropertyName.of(cached).prefix(prefix).starHack(starHack).copiedFrom(this);
        }
        return PropertyName.of(name()).starHack(starHack).copiedFrom(this);
    }

    /**
     * Creates a new {@link PropertyName} instance using the given string. Prefer to use {@link #of(Property)} instead.
     * <p/>
     * Please note that the property name will be automatically lower-cased.
     *
     * @param name
     *     The property name.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName of(String name) {
        checkNotNull(name, "name cannot be null");
        return of(-1, -1, name);
    }

    /**
     * Creates a new {@link PropertyName} from with the given String name. Prefer to use {@link #of(Property)} instead.
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
    public static PropertyName of(int line, int column, String name) {
        return new PropertyName(line, column, name);
    }

    /**
     * Creates a new {@link PropertyName} instance from the given {@link Property}.
     *
     * @param property
     *     The {@link Property} name.
     *
     * @return The new {@link PropertyName} instance.
     */
    public static PropertyName of(Property property) {
        return new PropertyName(-1, -1, property);
    }
}
