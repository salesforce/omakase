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

package com.salesforce.omakase.parser;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.*;

/**
 * Base class for testing parsers.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "unchecked"})
public abstract class AbstractParserTest<T extends Parser> implements ParserTest {
    @Rule public final ExpectedException exception = ExpectedException.none();
    protected final Parser parser;

    public AbstractParserTest() {
        try {
            Class<T> klass = (Class<T>)(new TypeToken<T>(getClass()) {}).getRawType();
            this.parser = klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error creating new instance of parser", e);
        }
    }

    /** A list of invalid sources. Note: this is be renamed to not applicable sources. */
    public abstract List<String> invalidSources();

    /**
     * A list of valid sources. It is expected for each source to be parsed completely, i.e., a successful parse should result in
     * {@link Source#eof()} being true.
     */
    public abstract List<String> validSources();

    /** A list of sources with the expected index after a successful parse. */
    public abstract List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex();

    /**
     * A valid source that upon parsing should result in at least one broadcaster AST object. See {@link
     * #lineAndColumnForSubStreams()}. Return null if no AST objects are expected to be broadcasted.
     */
    public abstract String validSourceForPositionTesting();

    /**
     * Whether the parser is allowed to trim whitespace even if it doesn't successfully parse. (not allowed in some cases where
     * whitespace can be significant, e.g., descendant combinator).
     */
    public abstract boolean allowedToTrimLeadingWhitespace();

    /**
     * The class of the main AST object being created. Override this if the first thing broadcasted from {@link
     * #validSourceForPositionTesting()} isn't the main AST object being tested.
     *
     * @return The class.
     */
    @SuppressWarnings("rawtypes")
    public Class<? extends Syntax> mainAstObjectClass() {
        return Syntax.class;
    }

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
        parser.parse(source, new Grammar(), broadcaster);

        @SuppressWarnings("rawtypes")
        Optional<? extends Syntax> syntax = broadcaster.find(mainAstObjectClass());
        if (!syntax.isPresent()) {
            fail("Test Error: expected source to broadcast a Syntax object");
        }

        assertThat(syntax.get().line()).isEqualTo(3);
        assertThat(syntax.get().column()).isEqualTo(2);
    }

    /** helper method */
    protected <T extends Broadcastable> T parse(Class<T> klass, String source) {
        SingleInterestBroadcaster<T> interest = new SingleInterestBroadcaster<T>(klass);
        parser.parse(new Source(source), new Grammar(), interest);
        if (!interest.one().isPresent()) {
            fail("did not find expected syntax unit");
        }
        return interest.one().get();
    }

    /** helper method */
    protected List<GenericParseResult> parse(String... sources) {
        return parse(Lists.newArrayList(sources));
    }

    /** helper method */
    protected List<GenericParseResult> parse(Iterable<String> sources) {
        List<GenericParseResult> results = new ArrayList<>();

        for (String source : sources) {
            GenericParseResult result = new GenericParseResult();
            result.broadcaster = new QueryableBroadcaster();
            result.source = new Source(source);
            result.success = parser.parse(result.source, new Grammar(), result.broadcaster);
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
        List<ParseResult<R>> results = new ArrayList<>();

        for (SourceWithExpectedResult<R> ts : sources) {
            ParseResult<R> result = new ParseResult<>();
            result.broadcaster = new QueryableBroadcaster();
            result.source = new Source(ts.source);
            result.success = parser.parse(result.source, new Grammar(), result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            result.broadcastedSyntax = result.broadcaster.filter(Syntax.class);
            result.expected = ts.expected;
            results.add(result);
        }

        return results;
    }

    protected <T extends Broadcastable> T expectOnly(QueryableBroadcaster broadcaster, Class<T> klass) {
        String msg = "expected to find exactly one instance of " + klass.getSimpleName();

        if (broadcaster.count() != 1) {
            fail(msg);
        }

        return broadcaster.find(klass).orElseThrow(() -> new AssertionError(msg));
    }

    /** helper object */
    public static class ParseResult<T> {
        public QueryableBroadcaster broadcaster;
        public Iterable<Broadcastable> broadcasted;
        @SuppressWarnings("rawtypes")
        public Iterable<Syntax> broadcastedSyntax;
        public boolean success;
        public Source source;
        public T expected;
    }

    /** helper object */
    public static final class GenericParseResult extends ParseResult<Object> {
    }
}
