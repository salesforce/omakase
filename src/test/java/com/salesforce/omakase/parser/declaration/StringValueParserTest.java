/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link StringValueParser}.
 * 
 * XXX the spec allows for escaped newlines in strings as well, see http://www.w3.org/TR/css3-values/#strings
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StringValueParserTest extends AbstractParserTest<StringValueParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "abc'",
            "afa",
            "afafa\"",
            "123af\"afaf\"faf",
            "afafafaf'ssgs'sgsgsg"
            );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "\"agg\"",
            "\"af\\\"afa\"",
            "'sfsfs'",
            "'sfsfsf\\'sfsfsf'",
            "\"121313\"",
            "\"this is a 'string'.\"",
            "\"this is a \\\"string\\\".\"",
            "'this is a \"string\".'",
            "'this is a \\'string\\'.'"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("\"this is a 'string'.\", afafaf", 22),
            withExpectedResult("\"this is a \\\"string\\\".\", afa'f\"ad", 24),
            withExpectedResult("'this is a \"string\".' 'afafafa'", 22),
            withExpectedResult("'this is a \\'string\\'.'\"afa\"", 24));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("\"this is a 'string'.\"", "this is a 'string'."),
            withExpectedResult("\"this is a \\\"string\\\".\"", "this is a \\\"string\\\"."),
            withExpectedResult("'this is a \"string\".'", "this is a \"string\"."),
            withExpectedResult("'this is a \\'string\\'.'", "this is a \\'string\\'."));

        for (ParseResult<String> result : results) {
            StringValue value = result.broadcaster.findOnly(StringValue.class).get();
            assertThat(value.content())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsOnUnclosedDoubleQuote() {
        final String msg = "Expected to find closing";

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("\"afafafafa");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("\"afafafafa\\\"");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("\"afafafafa'");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("\"afafafafa\\\"afafafafa\\\"afafa");
    }

    @Test
    public void errorsOnUnclosedSingleQuote() {
        final String msg = "Expected to find closing";

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("'afafafafaf");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("'afafafafaf\\'");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("'afafafafa\"");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("'asfasfs\\'asfasfas\\'sfsf");
    }
}
