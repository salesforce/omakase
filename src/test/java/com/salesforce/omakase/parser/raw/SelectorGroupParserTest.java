/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.raw;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link SelectorGroupParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class SelectorGroupParserTest extends AbstractParserTest<SelectorGroupParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "1,2",
            "$class",
            "",
            "\n");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#abc, #abc",
            ".class, .class",
            "*:hover, ::before",
            "p div, .classname",
            ".-anc",
            "a, :before");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(".class {", 7),
            withExpectedResult(".class, #id {", 12),
            withExpectedResult(".class,#id {", 11),
            withExpectedResult(".class.class.class {", 19),
            withExpectedResult("*{", 1),
            withExpectedResult(".class,\n.class2, \n.class3 {", 26));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("#abc, #abc", 2),
            withExpectedResult(".class, .class", 2),
            withExpectedResult("*:hover, ::before", 2),
            withExpectedResult(".class.class.class, \n  .class.class.class  ", 2),
            withExpectedResult("p div, .classname", 2),
            withExpectedResult(".class,\n.class2, \n.class3 ", 3));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.stream.toString()).hasSize(result.expected);
        }
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        GenericParseResult result = parse("   .class.class.class, \n  .class.class.class  ").get(0);
        assertThat(result.broadcasted).hasSize(2);
        assertThat(result.broadcasted.get(0)).isInstanceOf(Selector.class);
        assertThat(result.broadcasted.get(1)).isInstanceOf(Selector.class);
    }

    @Test
    public void errorsOnTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find a selector");
        parse("#abc,#abc, ");
    }
}
