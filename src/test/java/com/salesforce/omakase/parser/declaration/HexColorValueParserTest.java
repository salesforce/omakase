/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.value.HexColorValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link HexColorValueParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class HexColorValueParserTest extends AbstractParserTest<HexColorValueParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "ffffff",
            "123123",
            "fff123",
            "abc",
            " #123",
            " abc",
            "~123abc",
            "##ffffff",
            "##123123",
            "# ffffff");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#ffffff",
            "#132abc",
            "#999999",
            "#999",
            "#000000",
            "#987654",
            "#fefefe",
            "#FFF",
            "#F31FaB",
            "#ABCabc");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("#ffffff red", 8),
            withExpectedResult("#fff red", 5),
            withExpectedResult("#123456 #123456", 8),
            withExpectedResult("#ffeeff fff", 8),
            withExpectedResult("#abcfef red", 8),
            withExpectedResult("#defdef", 8),
            withExpectedResult("#123", 5),
            withExpectedResult("#AFE", 5),
            withExpectedResult("#000", 5));
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("#ffffff red", "ffffff"),
            withExpectedResult("#fff red", "fff"),
            withExpectedResult("#123456 #123456", "123456"),
            withExpectedResult("#ffeeff fff", "ffeeff"),
            withExpectedResult("#abcfef red", "abcfef"),
            withExpectedResult("#defdef", "defdef"),
            withExpectedResult("#123", "123"),
            withExpectedResult("#AFE", "afe"),
            withExpectedResult("#FFFfff", "ffffff"),
            withExpectedResult("#000", "000"));

        for (ParseResult<String> result : results) {
            HexColorValue hex = result.broadcaster.findOnly(HexColorValue.class).get();
            assertThat(hex.color()).isEqualTo(result.expected);
        }

    }

    @Test
    public void throwsErrorOnInvalidLength1() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#a");
    }

    @Test
    public void throwsErrorOnLength2() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#ab");
    }

    @Test
    public void throwsErrorOnLength4() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#aaaa");
    }

    @Test
    public void throwsErrorOnLength5() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#12345");
    }

    @Test
    public void throwsErrorOnLength7() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#1234567");
    }

    @Test
    public void throwsErrorOnLength8() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#123456ab");
    }
}
