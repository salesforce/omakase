/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.parser.AbstractParserTest;

/**
 * Unit tests for {@link UniversalSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class UniversalSelectorParserTest extends AbstractParserTest<UniversalSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return Lists.newArrayList(
            ".class",
            "#id",
            " *",
            "div*");
    }

    @Override
    public List<String> validSources() {
        return Lists.newArrayList(
            "*",
            "*#id",
            "*.cname");
    }

    @Override
    @Test
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            "* .class *",
            "*.class",
            "*#id");

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(1);
        }
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<GenericParseResult> results = parse("*");
        results.get(0).broadcaster.findOnly(UniversalSelector.class).get();
    }

    @Override
    @Test
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult("*#div", 2),
            withExpectedResult("*.class", 2),
            withExpectedResult("* class", 2));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }
}
