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
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;

/**
 * Utilities for working with {@link Parser}s.
 *
 * @author nmcwilliams
 * @see ParserFactory
 */
public final class Parsers {
    private Parsers() {}

    /**
     * Parses a {@link NumericalValue} at the beginning of the given string.
     *
     * @param source
     *     Parse this string.
     *
     * @return The {@link NumericalValue}, or {@link Optional#absent()} if not found.
     */
    public static Optional<NumericalValue> parseNumerical(String source) {
        return parseNumerical(new Source(source));
    }

    /**
     * Parses a {@link NumericalValue} at the beginning of the given source.
     *
     * @param source
     *     Parse this source.
     *
     * @return The {@link NumericalValue}, or {@link Optional#absent()} if not found.
     */
    public static Optional<NumericalValue> parseNumerical(Source source) {
        return parseSimple(source, ParserFactory.numericalValueParser(), NumericalValue.class);
    }

    /**
     * Uses the given parser to parse an instance of the given class at the beginning of the given source.
     * <p/>
     * The parser must find and broadcast an instance of the given type, and it must be at the beginning of the source.
     * <p/>
     * Example:
     * <pre><code>
     * Parsers.parseSimple("10px solid red", ParserFactory.numericalValueParser(), NumericalValue.class);
     * </code></pre>
     *
     * @param source
     *     The source to parse.
     * @param parser
     *     The parser to use.
     * @param klass
     *     The class of the object to parse.
     * @param <T>
     *     Type of the object to parse.
     *
     * @return The parsed instance, or {@link Optional#absent()} if not found.
     */
    public static <T extends Broadcastable> Optional<T> parseSimple(String source, Parser parser, Class<T> klass) {
        return parseSimple(new Source(source), parser, klass);
    }

    /**
     * Uses the given parser to parse an instance of the given class at the beginning of the given source.
     * <p/>
     * The parser must find and broadcast an instance of the given type, and it must be at the beginning of the source.
     * <p/>
     * Example:
     * <pre><code>
     * Parsers.parseSimple(source, ParserFactory.numericalValueParser(), NumericalValue.class);
     * </code></pre>
     *
     * @param source
     *     The source to parse.
     * @param parser
     *     The parser to use.
     * @param klass
     *     The class of the object to parse.
     * @param <T>
     *     Type of the object to parse.
     *
     * @return The parsed instance, or {@link Optional#absent()} if not found.
     */
    public static <T extends Broadcastable> Optional<T> parseSimple(Source source, Parser parser, Class<T> klass) {
        SingleInterestBroadcaster<T> broadcaster = SingleInterestBroadcaster.of(klass);
        parser.parse(source, broadcaster);
        return broadcaster.broadcasted();
    }
}
