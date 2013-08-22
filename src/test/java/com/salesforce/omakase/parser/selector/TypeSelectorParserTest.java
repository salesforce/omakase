/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.parser.AbstractParserTest;

/**
 * Unit tests for {@link TypeSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class TypeSelectorParserTest extends AbstractParserTest<TypeSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return Lists.newArrayList(
            "#id",
            ".class",
            ".p",
            ".class div",
            " div");
    }

    @Override
    public List<String> validSources() {
        return Lists.newArrayList(
            "p",
            "div p");
    }

    @Override
    @Test
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            "p",
            "p#div div",
            "div p");

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(1);
        }
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parse(
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

    @Override
    @Test
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult("p div", 2),
            withExpectedResult("p#div", 2),
            withExpectedResult("div.class", 4),
            withExpectedResult("div div div", 4));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }
}
