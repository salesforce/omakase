/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.parser.atrule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaRestriction;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaQueryParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryParserTest extends AbstractParserTest<MediaQueryParser> {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "  ",
            "\n",
            "$blah",
            "1test"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "all",
            "all and (min-width: 800px)",
            "only screen and (min-width: 800px)",
            "(min-width: 700px)",
            "(orientation: landscape) ",
            "tv and (min-width: 700px)",
            "  handheld and (orientation: landscape)",
            "all and (color)",
            "screen and (min-aspect-ratio: 1/1)",
            "(max-width: 15em)",
            "only SCREEN and (max-device-width: 480px)",
            "not screen AND (color)",
            "\tscreen and (min-width: 900px)",
            "screen and (min-width: 600px) and (max-width: 900px)",
            "screen and (min-width: 600px) AND (max-width: 900px)"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("all", 3),
            withExpectedResult("(min-width: 700px)  , handheld and (orientation: landscape)", 20),
            withExpectedResult("all and (min-width:800px  )", 27),
            withExpectedResult("screen and (device-aspect-ratio: 16/9), screen", 38),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px), all and (min-width:900px", 52));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("(color)", 2),
            withExpectedResult("(min-width:800px)", 3),
            withExpectedResult("all and (min-width:800px)", 3)
        ));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("(color)", ""),
            withExpectedResult("(min-width:800px)", ""),
            withExpectedResult("all", "all"),
            withExpectedResult("all and (min-width:800px)", "all"),
            withExpectedResult("only screen and (max-device-width: 480px)", "screen"),
            withExpectedResult("  handheld and (orientation: landscape)", "handheld"),
            withExpectedResult("not screen and (color)", "screen"),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px)", "screen")
        );

        for (ParseResult<String> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            if (result.expected.isEmpty()) {
                assertThat(mq.type().isPresent()).isFalse();
            } else {
                assertThat(mq.type().get()).isEqualTo(result.expected);
            }
        }
    }

    @Test
    public void matchesExpectedRestriction() {
        List<ParseResult<MediaRestriction>> results = parseWithExpected(
            TemplatesHelper.<MediaRestriction>withExpectedResult("(color)", null),
            TemplatesHelper.<MediaRestriction>withExpectedResult("(min-width:800px)", null),
            TemplatesHelper.<MediaRestriction>withExpectedResult("all", null),
            TemplatesHelper.<MediaRestriction>withExpectedResult("all and (min-width:800px)", null),
            TemplatesHelper.<MediaRestriction>withExpectedResult("  handheld and (orientation: landscape)", null),
            withExpectedResult("only screen and (max-device-width: 480px)", MediaRestriction.ONLY),
            withExpectedResult("ONLY screen and (max-device-width: 480px)", MediaRestriction.ONLY),
            withExpectedResult("not screen and (color)", MediaRestriction.NOT),
            withExpectedResult("NOT screen and (color)", MediaRestriction.NOT)
        );

        for (ParseResult<MediaRestriction> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            if (result.expected == null) {
                assertThat(mq.restriction().isPresent()).isFalse();
            } else {
                assertThat(mq.restriction().get()).isSameAs(result.expected);
            }
        }
    }

    @Test
    public void matchesExpectedExpressions() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("(color)", 1),
            withExpectedResult("(min-width:800px)", 1),
            withExpectedResult("all", 0),
            withExpectedResult("all and (min-width:800px)", 1),
            withExpectedResult("only screen and (max-device-width: 480px)", 1),
            withExpectedResult("  handheld and (orientation: landscape)", 1),
            withExpectedResult("not screen and (color)", 1),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px)", 2),
            withExpectedResult("handheld and (grid) and (max-width: 15em)", 2),
            withExpectedResult("handheld and (grid) AND (max-width: 15em)", 2)
        );

        for (ParseResult<Integer> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            assertThat(mq.expressions().size()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfRestrictionAndNoMediaType() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_MEDIA_TYPE.message());
        parse("only (min-width:800px");
    }

    @Test
    public void errorsIfRestrictionAndNoMediaTypeButHasAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_MEDIA_TYPE.message());
        parse("only and (min-width:800px");
    }

    @Test
    public void errorsIfMissingAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_AND.message());
        parse("all (min-width:800px)");
    }

    @Test
    public void errorsIfTrailingAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.TRAILING_AND.message());
        parse("all and (min-width:800px) and ");
    }

    @Test
    public void errorsIfTrailingAndNoExpression() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.TRAILING_AND.message());
        parse("all and ");
    }

    @Test
    public void errorsIfNoSpacAfterAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find");
        parse("all and(min-width:800px)");
    }
}
