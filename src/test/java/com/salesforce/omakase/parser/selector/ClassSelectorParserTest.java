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
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link ClassSelectorParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class ClassSelectorParserTest extends AbstractParserTest<ClassSelectorParser> {
    @Override
    List<String> invalidSources() {
        return Lists.newArrayList(
            fillSelector("#id"),
            fillSelector(" .class"),
            fillSelector("cla.ss"),
            fillSelector("#a.class"),
            fillSelector("class"));
    }

    @Override
    List<String> validSources() {
        return Lists.newArrayList(
            fillSelector(".class"),
            fillSelector(".CLASS"),
            fillSelector("._class"),
            fillSelector(".c1ass"),
            fillSelector(".-class"));
    }

    @Override
    @Test
    public void matchesExpectedBroadcastCount() {
        List<GenericParseResult> results = parse(
            fillSelector(".class"),
            fillSelector(".class .class"),
            fillSelector(".class.class2"));

        for (GenericParseResult result : results) {
            assertThat(result.broadcasted).hasSize(1);
        }
    }

    @Override
    @Test
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parse(
            withExpectedResult(fillSelector(".class"), "class"),
            withExpectedResult(fillSelector(".CLASS"), "CLASS"),
            withExpectedResult(fillSelector("._clasZ"), "_clasZ"),
            withExpectedResult(fillSelector(".-class-abc"), "-class-abc"),
            withExpectedResult(fillSelector(".claz1"), "claz1"));

        for (ParseResult<String> result : results) {
            ClassSelector selector = result.broadcaster.findOnly(ClassSelector.class).get();
            assertThat(selector.name()).isEqualTo(result.expected);
        }
    }

    @Override
    @Test
    public void expectedStreamPositionOnSuccess() {
        List<ParseResult<Integer>> results = parse(
            withExpectedResult(fillSelector(".class .class2"), 6),
            withExpectedResult(fillSelector(".class.class2"), 6),
            withExpectedResult(fillSelector(".class-abc-abc"), 15),
            withExpectedResult(fillSelector(".claz1#id"), 6));

        for (ParseResult<Integer> result : results) {
            assertThat(result.stream.column()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfInvalidClassNameAfterDot() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse(fillSelector(".#class"));

        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse(fillSelector("..class"));

        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid class name");
        parse(fillSelector(".9class"));
    }
}
