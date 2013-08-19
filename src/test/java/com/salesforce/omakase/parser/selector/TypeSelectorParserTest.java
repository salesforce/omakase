/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.fillSelector;
import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.TypeSelector;

/**
 * Unit tests for {@link TypeSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class TypeSelectorParserTest extends AbstractParserTest<TypeSelectorParser> {
    @Override
    List<String> invalidSources() {
        return Lists.newArrayList(
            fillSelector("#id"),
            fillSelector(".class"),
            fillSelector(".p"),
            fillSelector(".class div"),
            fillSelector(" div"));
    }

    @Override
    List<String> validSources() {
        return Lists.newArrayList(
            fillSelector("p"),
            fillSelector("div p"));
    }

    @Override
    @Test
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            fillSelector("p"),
            fillSelector("p#div div"),
            fillSelector("div p"));

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted).hasSize(1);
        }
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parse(
            withExpectedResult(fillSelector("p"), "p"),
            withExpectedResult(fillSelector("div"), "div"),
            withExpectedResult(fillSelector("somethingnew"), "somethingnew"));

        for (ParseResult<String> result : results) {
            TypeSelector selector = result.broadcaster.findOnly(TypeSelector.class).get();
            assertThat(selector.name()).isEqualTo(result.expected);
        }
    }

    @Override
    @Test
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult(fillSelector("p div"), 2),
            withExpectedResult(fillSelector("p#div"), 2),
            withExpectedResult(fillSelector("div.class"), 4),
            withExpectedResult(fillSelector("div div div"), 4));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column()).isEqualTo(result.expected);
        }
    }
}
