/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Combinator;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link CombinatorParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class CombinatorParserTest extends AbstractParserTest<CombinatorParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            ".class",
            "#id",
            "p div"
            );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            " .class",
            "   .class",
            "> .class",
            " > .class",
            ">.class",
            "+ div",
            " + .class",
            "+.class",
            "~ .class",
            " ~ #id",
            "~.class",
            "\n.class",
            "\t.class"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(" .class", 1),
            withExpectedResult("  .class", 2),
            withExpectedResult("> .class", 2),
            withExpectedResult(" > .class", 3),
            withExpectedResult(">.class", 1),
            withExpectedResult("   +   .class", 7),
            withExpectedResult("   +.class", 4),
            withExpectedResult("~.class", 1));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<SelectorPartType>> results = parseWithExpected(
            withExpectedResult(" .class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("   .class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("> .class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult(" > .class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult(">.class", SelectorPartType.CHILD_COMBINATOR),
            withExpectedResult("+ div", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult(" + .class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("    +    .class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("+.class", SelectorPartType.ADJACENT_SIBLING_COMBINATOR),
            withExpectedResult("~ .class", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult(" ~ #id", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult("~.class", SelectorPartType.GENERAL_SIBLING_COMBINATOR),
            withExpectedResult("\n.class", SelectorPartType.DESCENDANT_COMBINATOR),
            withExpectedResult("\t.class", SelectorPartType.DESCENDANT_COMBINATOR));

        for (ParseResult<SelectorPartType> result : results) {
            Combinator combinator = result.broadcaster.findOnly(Combinator.class).get();
            assertThat(combinator.type()).describedAs(result.stream.toString()).isEqualTo(result.expected);
        }
    }

    @Test
    /** overridden because whitespace can be a descendant combinator */
    public void correctLineAndColumnNumber() {
        List<GenericParseResult> results = parse(validSources());
        for (GenericParseResult result : results) {
            Syntax first = result.broadcasted.get(0);
            assertThat(first.line())
                .describedAs(result.stream.toString())
                .isEqualTo(1);
            assertThat(first.column())
                .describedAs(result.stream.toString())
                .isEqualTo(1);
        }
    }
}
