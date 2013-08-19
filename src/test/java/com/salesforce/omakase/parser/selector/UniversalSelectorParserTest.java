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
import com.salesforce.omakase.ast.selector.UniversalSelector;

/**
 * Unit tests for {@link UniversalSelectorParser}.
 * 
 * @author nmcwilliams
 */
public class UniversalSelectorParserTest extends AbstractParserTest<UniversalSelectorParser> {
    @Override
    List<String> invalidSources() {
        return Lists.newArrayList(
            fillSelector(".class"),
            fillSelector("#id"),
            fillSelector(" *"),
            fillSelector("div*"));
    }

    @Override
    List<String> validSources() {
        return Lists.newArrayList(
            fillSelector("*"),
            fillSelector("*#id"),
            fillSelector("*.cname"));
    }

    @Override
    @Test
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            fillSelector("* .class *"),
            fillSelector("*.class"),
            fillSelector("*#id"));

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted).hasSize(1);
        }
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<GenericParseResult> results = parse(fillSelector("*"));
        results.get(0).broadcaster.findOnly(UniversalSelector.class).get();
    }

    @Override
    @Test
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult(fillSelector("*#div"), 2),
            withExpectedResult(fillSelector("*.class"), 2),
            withExpectedResult(fillSelector("* class"), 2));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column()).isEqualTo(result.expected);
        }
    }
}
