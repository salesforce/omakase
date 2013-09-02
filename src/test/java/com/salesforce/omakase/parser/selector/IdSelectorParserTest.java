/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link IdSelectorParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
public class IdSelectorParserTest extends AbstractParserTest<IdSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            ".class",
            "p div",
            "a:link",
            "._class",
            ".class #id",
            "p#id",
            " #id");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#id",
            "#ID",
            "#_id",
            "#_1",
            "#_1id",
            "#id123",
            "#-name",
            "#-NAMEname1_aAz234ABCdefafklsjfseufhuise____hfie");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("#id .class", 3),
            withExpectedResult("#ID #id", 3),
            withExpectedResult("#_id>p>div#id", 4),
            withExpectedResult("#_1  ", 3),
            withExpectedResult("#_1id", 5),
            withExpectedResult("#id123", 6),
            withExpectedResult("#-name~a", 6),
            withExpectedResult("#-NAMEname1_aAz234ABCdefafklsjfseuf+.huise____hfie", 35));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("#id .class", "id"),
            withExpectedResult("#ID #id", "ID"),
            withExpectedResult("#_id>p>div#id", "_id"),
            withExpectedResult("#_1  ", "_1"),
            withExpectedResult("#_1id", "_1id"),
            withExpectedResult("#id123", "id123"),
            withExpectedResult("#-name~a", "-name"),
            withExpectedResult("#-NAMEname1_aAz234ABCdefafklsjfseuf+.huise____hfie", "-NAMEname1_aAz234ABCdefafklsjfseuf"));

        for (ParseResult<String> result : results) {
            IdSelector selector = result.broadcaster.findOnly(IdSelector.class).get();
            assertThat(selector.name())
                .describedAs(result.stream.toString())
                .isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfDoubleHash() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("##id");
    }

    @Test
    public void errorsIfDoubleDash() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#--abc");
    }

    @Test
    public void errorsIfDashNumber() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#-1abc");
    }

    @Test
    public void errorsIfDashDot() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("#.class");
    }

    @Test
    public void errorsIfSpace() {
        exception.expect(ParserException.class);
        exception.expectMessage("expected to find a valid id name");
        parse("# abc");
    }
}
