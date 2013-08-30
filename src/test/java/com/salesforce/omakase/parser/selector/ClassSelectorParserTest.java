/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link ClassSelectorParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class ClassSelectorParserTest extends AbstractParserTest<ClassSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "#id",
            " .class",
            "cla.ss",
            "#a.class",
            "class");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            ".class",
            ".CLASS",
            "._class",
            ".c1ass",
            ".-class",
            "._NAMEname1_aAz234ABCdefafklsjfseufhuise____hfie");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(".class .class2", 7),
            withExpectedResult(".class.class2", 7),
            withExpectedResult(".class-abc-abc", 15),
            withExpectedResult(".claz#id", 6));
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
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
    public void errorsIfInvalidClassNameAfterDot() {
        final String msg = "expected to find a valid class name";

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse(".#class");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse("..class");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse(".9class");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse(".-9class");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse(".--class");

        exception.expect(ParserException.class);
        exception.expectMessage(msg);
        parse(". class");
    }
}
