/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Base class for testing parsers.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings({ "javadoc", "serial", "unchecked" })
public abstract class AbstractParserTest<T extends Parser> implements ParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Class<T> klass = (Class<T>)(new TypeToken<T>(getClass()) {}).getRawType();
    private final Parser parser;

    public AbstractParserTest() {
        try {
            this.parser = klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error creating new instance of parser", e);
        }
    }

    public abstract List<String> invalidSources();

    public abstract List<String> validSources();

    public abstract List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex();

    public abstract boolean allowedToTrimLeadingWhitespace();

    @Test
    @Override
    public void returnsFalseOnFailure() {
        List<GenericParseResult> results = parse(invalidSources());
        for (GenericParseResult result : results) {
            assertThat(result.success).describedAs(result.stream.toString()).isFalse();
        }
    }

    @Test
    @Override
    public void returnsTrueOnSuccess() {
        List<GenericParseResult> results = parse(validSources());
        for (GenericParseResult result : results) {
            assertThat(result.success).describedAs(result.stream.toString()).isTrue();
        }
    }

    /**
     * Override in subclass if expected count isn't 1.
     */
    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        for (GenericParseResult result : parse(validSources())) {
            assertThat(result.broadcasted).describedAs(result.stream.toString()).hasSize(1);
        }
    }

    @Test
    @Override
    public void noChangeToStreamOnFailure() {
        for (GenericParseResult result : parse(invalidSources())) {
            // parsers skipping past whitespace is ok
            if (allowedToTrimLeadingWhitespace()) {
                final String source = result.stream.source();
                int index = source.length() - CharMatcher.WHITESPACE.trimLeadingFrom(source).length();
                assertThat(result.stream.index()).describedAs(result.stream.toString()).isEqualTo(index);
            } else {
                assertThat(result.stream.index()).describedAs(result.stream.toString()).isEqualTo(0);
            }
        }
    }

    @Test
    @Override
    public void expectedStreamPositionOnSuccess() {
        for (ParseResult<Integer> result : parseWithExpected(validSourcesWithExpectedEndIndex())) {
            assertThat(result.stream.index()).describedAs(result.stream.toString()).isEqualTo(result.expected);
        }
    }

    @Test
    @Override
    public void correctLineAndColumnNumber() {
        for (GenericParseResult result : parse(validSources())) {
            Syntax first = result.broadcasted.get(0);

            assertThat(first.line()).describedAs(result.stream.toString()).isEqualTo(1);

            String trim = result.stream.source().trim();
            int column = result.stream.source().indexOf(trim) + 1;

            assertThat(first.column()).describedAs(result.stream.toString()).isEqualTo(column);
        }
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
            result.stream = new Stream(source);
            result.success = parser.parse(result.stream, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
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
            result.stream = new Stream(ts.source);
            result.success = parser.parse(result.stream, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            result.expected = ts.expected;
            results.add(result);
        }

        return results;
    }

    /** helper object */
    public static class ParseResult<T> {
        public QueryableBroadcaster broadcaster;
        public List<Syntax> broadcasted;
        public boolean success;
        public Stream stream;
        public T expected;
    }

    /** helper object */
    public static final class GenericParseResult extends ParseResult<Object> {
    }
}
