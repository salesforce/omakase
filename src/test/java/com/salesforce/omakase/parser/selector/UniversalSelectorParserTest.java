/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

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
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("*#div", 2),
            withExpectedResult("*.class", 2),
            withExpectedResult("* class", 2));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<GenericParseResult> results = parse("*");
        results.get(0).broadcaster.findOnly(UniversalSelector.class).get();
    }
}
