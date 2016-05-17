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
     * <p>
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
     * <p>
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
