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

package com.salesforce.omakase.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.fest.assertions.api.Assertions.*;

/**
 * Base class for testing parsers.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "unchecked"})
public abstract class AbstractParserTest<T extends Parser> implements ParserTest {
    @Rule public final ExpectedException exception = ExpectedException.none();
    private final Parser parser;

    public AbstractParserTest() {
        try {
            Class<T> klass = (Class<T>)(new TypeToken<T>(getClass()) {}).getRawType();
            this.parser = klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error creating new instance of parser", e);
        }
    }

    /** A list of invalid sources. */
    public abstract List<String> invalidSources();

    /**
     * A list of valid sources. It is expected for each source to be parsed completely, i.e., a successful parse should result in
     * {@link Source#eof()} being true.
     */
    public abstract List<String> validSources();

    /**
     * A valid source that upon parsing should result in at least one broadcaster AST object. See {@link
     * #lineAndColumnForSubStreams()}. Return null if no AST objects are expected to be broadcasted.
     */
    public abstract String validSourceForPositionTesting();

    /** A list of sources with the expected index after a successful parse. */
    public abstract List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex();

    /**
     * Whether the parser is allowed to trim whitespace even if it doesn't successfully parse. (not allowed in some cases where
     * whitespace can be significant, e.g., descendant combinator). ==
     */
    public abstract boolean allowedToTrimLeadingWhitespace();

    @Test
    @Override
    public void returnsFalseOnFailure() {
        List<GenericParseResult> results = parse(invalidSources());
        for (GenericParseResult result : results) {
            assertThat(result.success).describedAs(result.source.toString()).isFalse();
        }
    }

    @Test
    @Override
    public void returnsTrueOnSuccess() {
        List<GenericParseResult> results = parse(validSources());
        for (GenericParseResult result : results) {
            assertThat(result.success).describedAs(result.source.toString()).isTrue();
        }
    }

    @Test
    @Override
    public void eofOnValidSources() {
        List<GenericParseResult> results = parse(validSources());
        for (GenericParseResult result : results) {
            assertThat(result.source.eof()).describedAs(result.source.toString()).isTrue();
        }
    }

    /** Override in subclass if expected count isn't 1. */
    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        for (GenericParseResult result : parse(validSources())) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(1);
        }
    }

    @Test
    @Override
    public void noChangeToStreamOnFailure() {
        for (GenericParseResult result : parse(invalidSources())) {
            // parsers skipping past whitespace is ok
            if (allowedToTrimLeadingWhitespace()) {
                final String source = result.source.fullSource();
                int index = source.length() - CharMatcher.WHITESPACE.trimLeadingFrom(source).length();
                assertThat(result.source.index()).describedAs(result.source.toString()).isEqualTo(index);
            } else {
                assertThat(result.source.index()).describedAs(result.source.toString()).isEqualTo(0);
            }
        }
    }

    @Test
    @Override
    public void expectedStreamPositionOnSuccess() {
        for (ParseResult<Integer> result : parseWithExpected(validSourcesWithExpectedEndIndex())) {
            assertThat(result.source.index()).describedAs(result.source.toString()).isEqualTo(result.expected);
        }
    }

    @Test
    @Override
    public void lineAndColumnForSubStreams() {
        String content = validSourceForPositionTesting();
        if (content == null) return;

        Source source = new Source(content, 3, 2);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        parser.parse(source, broadcaster);

        Optional<Syntax> syntax = broadcaster.find(Syntax.class);
        if (!syntax.isPresent()) {
            fail("Test Error: expected source to broadcast a Syntax object");
        }

        assertThat(syntax.get().line()).isEqualTo(3);
        assertThat(syntax.get().column()).isEqualTo(2);
    }

    /** helper method */
    protected List<GenericParseResult> parse(String... sources) {
        return parse(Lists.newArrayList(sources));
    }

    /** helper method */
    protected List<GenericParseResult> parse(Iterable<String> sources) {
        List<GenericParseResult> results = Lists.newArrayList();

        for (String source : sources) {
            GenericParseResult result = new GenericParseResult();
            result.broadcaster = new QueryableBroadcaster();
            result.source = new Source(source);
            result.success = parser.parse(result.source, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            result.broadcastedSyntax = result.broadcaster.filter(Syntax.class);
            results.add(result);
        }

        return results;
    }

    /** helper method */
    @SafeVarargs
    protected final <R> List<ParseResult<R>> parseWithExpected(SourceWithExpectedResult<R>... sources) {
        return parseWithExpected(Lists.newArrayList(sources));
    }

    /** helper method */
    protected final <R> List<ParseResult<R>> parseWithExpected(Iterable<SourceWithExpectedResult<R>> sources) {
        List<ParseResult<R>> results = Lists.newArrayList();

        for (SourceWithExpectedResult<R> ts : sources) {
            ParseResult<R> result = new ParseResult<>();
            result.broadcaster = new QueryableBroadcaster();
            result.source = new Source(ts.source);
            result.success = parser.parse(result.source, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            result.broadcastedSyntax = result.broadcaster.filter(Syntax.class);
            result.expected = ts.expected;
            results.add(result);
        }

        return results;
    }

    /** helper object */
    public static class ParseResult<T> {
        public QueryableBroadcaster broadcaster;
        public Iterable<Broadcastable> broadcasted;
        public Iterable<Syntax> broadcastedSyntax;
        public boolean success;
        public Source source;
        public T expected;
    }

    /** helper object */
    public static final class GenericParseResult extends ParseResult<Object> {
    }
}
