/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link KeywordValueParser}.
 * 
 * @author nmcwilliams
 */
public class KeywordValueParserTest extends AbstractParserTest<KeywordValueParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "'abc",
            "123",
            "--abs",
            "-1afafa",
            "123ABC",
            "  afafk",
            " 123"
            );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "abc",
            "ABC",
            "AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d",
            "-abc-afakl-afa",
            "-afafma-afaf-",
            "-ADA",
            "_afjahfkahf123123",
            "a812313",
            "__afhafa_____------____-afafa",
            "_1afa",
            "a1a2f3g4_",
            "media",
            "red",
            "red11re"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("abc 123", 3),
            withExpectedResult("abc1234", 7),
            withExpectedResult("abc abc abc", 3),
            withExpectedResult("BLACK", 5),
            withExpectedResult("-afauf-afaf___afaf _af", 18),
            withExpectedResult("_1afafkslf", 10),
            withExpectedResult("afa____--___---___-afafaf---123123afafa afafa", 39),
            withExpectedResult("red 1red-1red red", 3));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("abc 123  ", "abc"),
            withExpectedResult("ABC__A_", "ABC__A_"),
            withExpectedResult("AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d",
                "AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d"),
            withExpectedResult("_1afa ", "_1afa"),
            withExpectedResult("red11re", "red11re"),
            withExpectedResult("-ADA", "-ADA"));

        for (ParseResult<String> result : results) {
            KeywordValue value = result.broadcaster.findOnly(KeywordValue.class).get();
            assertThat(value.keyword())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }
}
