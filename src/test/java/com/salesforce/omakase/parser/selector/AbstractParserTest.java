/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.salesforce.omakase.CollectingBroadcaster;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserTest;
import com.salesforce.omakase.parser.Stream;
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

    abstract List<String> invalidSources();

    abstract List<String> validSources();

    @Test
    @Override
    public void returnsFalseOnFailure() {
        List<GenericParseResult> results = parse(invalidSources().toArray(new String[] {}));
        for (GenericParseResult result : results) {
            assertThat(result.success).isFalse();
        }
    }

    @Test
    @Override
    public void returnsTrueOnSuccess() {
        List<GenericParseResult> results = parse(validSources().toArray(new String[] {}));
        for (GenericParseResult result : results) {
            assertThat(result.success).isTrue();
        }
    }

    @Test
    @Override
    public void noChangeToStreamOnFailure() {
        List<GenericParseResult> results = parse(invalidSources().toArray(new String[] {}));
        for (GenericParseResult result : results) {
            assertThat(result.stream.line()).isEqualTo(1);
            assertThat(result.stream.column()).isEqualTo(1);
        }
    }

    /** helper method */
    protected List<GenericParseResult> parse(String... sources) {
        List<GenericParseResult> results = Lists.newArrayList();

        for (String source : sources) {
            GenericParseResult result = new GenericParseResult();
            result.broadcaster = new CollectingBroadcaster();
            result.stream = new Stream(source);
            result.success = parser.parse(result.stream, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            results.add(result);
        }

        return results;
    }

    @SafeVarargs
    /** helper method */
    protected final <R> List<ParseResult<R>> parse(SourceWithExpectedResult<R>... sources) {
        List<ParseResult<R>> results = Lists.newArrayList();

        for (SourceWithExpectedResult<R> ts : sources) {
            ParseResult<R> result = new ParseResult<>();
            result.broadcaster = new CollectingBroadcaster();
            result.stream = new Stream(ts.source);
            result.success = parser.parse(result.stream, result.broadcaster);
            result.broadcasted = result.broadcaster.all();
            result.expected = ts.expected;
        }

        return results;
    }

    /** helper object */
    public static class ParseResult<T> {
        CollectingBroadcaster broadcaster;
        List<Syntax> broadcasted;
        boolean success;
        Stream stream;
        T expected;
    }

    /** helper object */
    public static final class GenericParseResult extends ParseResult<Object> {
    }
}
