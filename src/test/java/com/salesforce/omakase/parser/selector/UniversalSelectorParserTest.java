/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.test.util.Templates.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.Templates.withExpectedResult;

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
        return Lists.newArrayList("*");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("*#div", 1),
            withExpectedResult("*.class", 1),
            withExpectedResult("* class", 1));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<GenericParseResult> results = parse("*");
        results.get(0).broadcaster.findOnly(UniversalSelector.class).get();
    }
}
