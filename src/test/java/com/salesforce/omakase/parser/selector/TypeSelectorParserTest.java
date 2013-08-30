/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link TypeSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class TypeSelectorParserTest extends AbstractParserTest<TypeSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "#id",
            ".class",
            ".p",
            ".class div",
            " div",
            "*");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "p",
            "div p",
            "a:link");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("p div", 2),
            withExpectedResult("p#div", 2),
            withExpectedResult("div.class", 4),
            withExpectedResult("div div div", 4));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("p", "p"),
            withExpectedResult("div", "div"),
            withExpectedResult("somethingnew", "somethingnew"));

        for (ParseResult<String> result : results) {
            TypeSelector selector = result.broadcaster.findOnly(TypeSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }
}
