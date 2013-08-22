/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link ClassSelectorParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class ClassSelectorParserTest extends AbstractParserTest<ClassSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return Lists.newArrayList(
            "#id",
            " .class",
            "cla.ss",
            "#a.class",
            "class");
    }

    @Override
    public List<String> validSources() {
        return Lists.newArrayList(
            ".class",
            ".CLASS",
            "._class",
            ".c1ass",
            ".-class");
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            ".class",
            ".class .class",
            ".class.class2");

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted)
                .describedAs(result.stream.toString())
                .hasSize(1);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parse(
            withExpectedResult(".class", "class"),
            withExpectedResult(".CLASS", "CLASS"),
            withExpectedResult("._clasZ", "_clasZ"),
            withExpectedResult(".-class-abc", "-class-abc"),
            withExpectedResult(".claz1", "claz1"));

        for (ParseResult<String> result : results) {
            ClassSelector selector = result.broadcaster.findOnly(ClassSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    @Override
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult(".class .class2", 7),
            withExpectedResult(".class.class2", 7),
            withExpectedResult(".class-abc-abc", 15),
            withExpectedResult(".claz#id", 6));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfInvalidClassNameAfterDot() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse(".#class");

        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse("..class");

        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse(".9class");
    }
}
