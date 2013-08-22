/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link StringValueParser}.
 * 
 * TODO the spec allows for escaped newlines in strings as well, see http://www.w3.org/TR/css3-values/#strings
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class StringValueParserTest extends AbstractParserTest<StringValueParser> {

    @Override
    public List<String> invalidSources() {
        return Lists.newArrayList(
            "abc'",
            "afa",
            "afafa\"",
            "123af\"afaf\"faf",
            "afafafaf'ssgs'sgsgsg"
            );
    }

    @Override
    public List<String> validSources() {
        return Lists.newArrayList(
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

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            "\"this is a 'string'.\"",
            "\"this is a \\\"string\\\".\"",
            "'this is a \"string\".'",
            "'this is a \\'string\\'.'");

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(1);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parse(
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
    @Override
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult("\"this is a 'string'.\", afafaf", 22),
            withExpectedResult("\"this is a \\\"string\\\".\", afa'f\"ad", 24),
            withExpectedResult("'this is a \"string\".' 'afafafa'", 22),
            withExpectedResult("'this is a \\'string\\'.'\"afa\"", 24));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsInUnclosedDoubleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa\\\"");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa'");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("\"afafafafa\\\"afafafafa\\\"afafa");
    }

    @Test
    public void errorsOnUnclosedSingleQuote() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafaf");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafaf\\'");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'afafafafa\"");

        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("'asfasfs\\'asfasfas\\'sfsf");
    }
}
