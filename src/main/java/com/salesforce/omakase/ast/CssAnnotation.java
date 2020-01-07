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

package com.salesforce.omakase.ast;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.salesforce.omakase.error.OmakaseException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a CSS annotation comment.
 * <p>
 * CSS comment annotations are CSS comments that contain an annotation in the format of "@annotationName [optionalArgs]", for
 * example "@noparse", "@browser ie7", "@something arg1 arg2 arg3", etc...
 * <p>
 * There are methods to support various types of arg formats, including space-separated, comma-separated and enum-based. More
 * complex, custom formats can manually be parsed via the {@link CssAnnotation#rawArgs()} method.
 * <p>
 * Syntax units are associated with the CSS  comments that directly precede them. More than one CSS annotation comment is allowed.
 * See the main readme for more information on comments.
 *
 * @author nmcwilliams
 */
public final class CssAnnotation {
    private final String name;
    private final String args;

    private Comment cached;

    /**
     * Creates a new {@link CssAnnotation} with the given name.
     *
     * @param name
     *     The annotation name.
     */
    public CssAnnotation(String name) {
        this(name, null);
    }

    /**
     * Creates a new {@link CssAnnotation} instance with the given name and arguments.
     *
     * @param name
     *     The name of the annotation.
     * @param args
     *     Optional arguments (automatically trimmed).
     */
    public CssAnnotation(String name, String args) {
        this.name = checkNotNull(name, "name cannot be null");
        this.args = args != null ? args.trim() : null;
    }

    /**
     * Gets the name of the annotation.
     *
     * @return The name of the annotation.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the raw, unprocessed args.
     *
     * @return The raw, unprocessed args.
     */
    public Optional<String> rawArgs() {
        return Optional.ofNullable(args);
    }

    /**
     * Parses the arguments using spaces as the delimiter. Results will be trimmed and will not contain any empty strings.
     * <p>
     * For example, given the following css annotation:
     * <pre><code>
     *     {@literal @}foo one two three four
     * </code></pre>
     * The arguments returned will be a list with values "one", "two", "three", and "four".
     *
     * @return The parsed list of arguments, or an empty list if no arguments are present.
     */
    public ImmutableList<String> spaceSeparatedArgs() {
        if (args != null) {
            Iterable<String> split = Splitter.on(" ").omitEmptyStrings().trimResults().split(args);
            return ImmutableList.copyOf(split);
        }
        return ImmutableList.of();
    }

    /**
     * Parses the arguments using commas as the delimiter. Results will be trimmed and will not contain any empty strings.
     * <p>
     * For example, given the following css annotation:
     * <pre><code>
     *     {@literal @}foo one, two, three, four
     * </code></pre>
     * The arguments returned will be a list with values "one", "two", "three", and "four".
     *
     * @return The parsed list of arguments, or an empty list if no arguments are present.
     */
    public ImmutableList<String> commaSeparatedArgs() {
        if (args != null) {
            return ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().trimResults().split(args));
        }
        return ImmutableList.of();
    }

    /**
     * Parsers the arguments for key value pairs. Results will be trimmed and will not contain any empty strings.
     * <p>
     * The key value pairs are separated by commas. For example, given any of the following css annotations:
     * <pre><code>
     *     {@literal @}foo bar=baz, bim=bop, bip=beep
     *     {@literal @}foo bar = baz, bim = bop, bip = beep
     *     {@literal @}foo bar:baz, bim:bop, bip:beep
     *     {@literal @}foo bar baz, bim bop, bip beep
     * </code></pre>
     * The arguments returned will be a map with values {"bar"="baz", "bim"="bop", "bip"="beep"}.
     * <p>
     * Optionally, the first arg may omit the key, and it will be placed in the map under "name". For example:
     * <pre><code>
     *     {@literal @}foo bar, bim bop, bip beep
     * </code></pre>
     * The arguments returned will be a map with values {"name"="bar", "bim"="bop", "bip"="boop"}
     *
     * @param keyValueSeparator
     *     The character separating key-value pairs, usually ' ', '=' or ':'. Spaces around this character are allowed.
     * @return The parsed map of arguments, or an empty map if no arguments are present.
     */
    public ImmutableMap<String, String> keyValueArgs(char keyValueSeparator) {
        if (args != null && !args.isEmpty()) {
            String toParse = args;

            // check for implied name
            int firstComma = toParse.indexOf(',');
            int firstDelimiter = toParse.indexOf(keyValueSeparator);

            if ((firstComma == -1 && firstDelimiter == -1) || (firstComma != -1 && firstComma < firstDelimiter)) {
                toParse = "name" + keyValueSeparator + toParse;
            }

            try {
                return ImmutableMap.copyOf(
                    Splitter.on(",")
                        .omitEmptyStrings()
                        .trimResults()
                        .withKeyValueSeparator(Splitter.on(keyValueSeparator).trimResults())
                        .split(toParse));
            } catch (IllegalArgumentException e) {
                throw new OmakaseException("unable to parse CSS comment annotation: '" + args + "'", e);
            }

        }
        return ImmutableMap.of();
    }

    /**
     * Same as {@link CssAnnotation#fromEnum(Class, CaseFormat, CaseFormat)}, except this method only works with enums that use
     * TITLE_CASE, and annotations that either use TITLE_CASE or lowerCamel.
     *
     * @param enumClass
     *     The enum class.
     * @param <E>
     *     The enum type.
     * @return The set of enum values found, or an empty set if no arguments are present.
     */
    public <E extends Enum<E>> EnumSet<E> fromEnum(Class<E> enumClass) {
        if (args != null) {
            CaseFormat fmt = Character.isLowerCase(args.charAt(0)) ? CaseFormat.LOWER_CAMEL : CaseFormat.UPPER_UNDERSCORE;
            return fromEnum(enumClass, CaseFormat.UPPER_UNDERSCORE, fmt);
        }
        return EnumSet.noneOf(enumClass);
    }

    /**
     * Parses the arguments with allowed values from the given enum.
     * <p>
     * The values must be space-separated and any value not present in the enum will result in an error. For example, with the
     * enum {ONE, TWO, THREE}, the following:
     * <pre><code>
     *     {@literal @}foo one two
     * </code></pre>
     * would return a map containing ONE and TWO.
     * <p>
     * The names must be an exact match, however they will be normalized using the given CaseFormats.
     *
     * @param enumClass
     *     The enum class.
     * @param enumFormat
     *     The format of the enum.
     * @param annotationFormat
     *     The format used by the css annotations.
     * @param <E>
     *     The enum type.
     * @return The set of enum values found, or an empty set if no arguments are present.
     * @throws IllegalArgumentException
     *     for annotation arguments without a matching enum entry.
     */
    public <E extends Enum<E>> EnumSet<E> fromEnum(Class<E> enumClass, CaseFormat enumFormat, CaseFormat annotationFormat) {
        if (args != null) {
            EnumSet<E> set = EnumSet.noneOf(enumClass);

            for (String arg : Splitter.on(' ').trimResults().omitEmptyStrings().split(args)) {
                String normalized = annotationFormat.to(enumFormat, arg);
                try {
                    E match = Enum.valueOf(enumClass, normalized);
                    set.add(match);
                } catch (IllegalArgumentException e) {
                    String msg = "Invalid CSS annotation argument '%s', must be one of '%s'";
                    throw new IllegalArgumentException(String.format(msg, arg, Arrays.toString(enumClass.getEnumConstants())), e);
                }
            }

            return set;
        }
        return EnumSet.noneOf(enumClass);
    }

    /**
     * Creates a new {@link Comment} instance using this annotation as the content.
     *
     * @param useCached
     *     If true, this method will reuse the same {@link Comment} instance previously created from this method instead of
     *     creating a new one. As {@link Comment} objects are essentially immutable, you usually want to pass true here.
     * @return The new {@link Comment} instance.
     */
    public Comment toComment(boolean useCached) {
        if (useCached) {
            if (cached == null) {
                cached = new Comment(this);
            }
            return cached;
        }

        return new Comment(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("@").append(name);
        if (args != null) {
            builder.append(' ').append(args);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CssAnnotation) {
            CssAnnotation other = (CssAnnotation)object;
            return Objects.equals(name, other.name) && Objects.equals(args, other.args);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }
}
