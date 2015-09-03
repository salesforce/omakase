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

package com.salesforce.omakase.util;

import com.google.common.base.Optional;
import com.salesforce.omakase.data.Prefix;

/**
 * Utilities for working with vendor prefixes.
 *
 * @author nmcwilliams
 */
public final class Prefixes {
    private Prefixes() {}

    /**
     * Finds the vendor {@link Prefix} at the start of the given string.
     *
     * @param name
     *     Find the prefix at the start of this string.
     *
     * @return The {@link Prefix}, or {@link Optional#absent()} if a prefix is not present.
     */
    public static Optional<Prefix> parsePrefix(String name) {
        for (Prefix prefix : Prefix.values()) {
            if (name.startsWith(prefix.toString())) return Optional.of(prefix);
        }
        return Optional.absent();
    }

    /**
     * Splits a string into the vendor {@link Prefix} and unprefixed portions.
     * <p/>
     * If the string does not contain a prefix then the returned {@link PrefixPair} will return {@link Optional#absent()} from the
     * {@link PrefixPair#prefix()} method.
     *
     * @param name
     *     Split this string.
     *
     * @return A new {@link PrefixPair} instance, containing the {@link Prefix} (if found) and the unprefixed portion.
     */
    public static PrefixPair splitPrefix(String name) {
        if (name.charAt(0) == '-') {
            int end = name.indexOf("-", 1);
            if (end > -1) {
                Optional<Prefix> prefix = parsePrefix(name.substring(0, end + 1));
                if (!prefix.isPresent()) throw new IllegalArgumentException("unknown prefix in " + name);
                return new PrefixPair(prefix.get(), name.substring(end + 1));
            }
        }
        return new PrefixPair(null, name);
    }

    /**
     * Gets the unprefixed portion of the given name.
     * <p/>
     * If the name is not prefixed, it will be returned unchanged. If the name is prefixed, the unprefixed part of the name will
     * be returned.
     *
     * @param name
     *     Find the unprefixed part of this string.
     *
     * @return The unprefixed part of the string, or the string itself if it is not prefixed.
     */
    public static String unprefixed(String name) {
        if (name.charAt(0) != '-') return name;
        return splitPrefix(name).unprefixed();
    }

    /** Represents a name with an optional vendor {@link Prefix}. */
    public static final class PrefixPair {
        private final Prefix prefix;
        private final String unprefixed;

        private PrefixPair(Prefix prefix, String unprefixed) {
            this.prefix = prefix;
            this.unprefixed = unprefixed;
        }

        /**
         * Gets the {@link Prefix}.
         *
         * @return The {@link Prefix}, or {@link Optional#absent()} if a prefix was not present.
         */
        public Optional<Prefix> prefix() {
            return Optional.fromNullable(prefix);
        }

        /**
         * Gets the remainder of the name after the prefix, or the whole name if a prefix was not present.
         *
         * @return The unprefixed part of the name.
         */
        public String unprefixed() {
            return unprefixed;
        }
    }
}
