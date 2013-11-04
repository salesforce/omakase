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
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.salesforce.omakase.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaQueryListParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryListParserTest extends AbstractParserTest<MediaQueryListParser> {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            ".",
            "  ",
            "1",
            "  1"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "all and (min-color: 4)",
            "screen, projection",
            "all and (max-width:800px), screen and (color)",
            "(min-width: 700px), handheld and (orientation: landscape)",
            "screen and (device-aspect-ratio: 16/9), screen and (device-aspect-ratio: 16/10)",
            "only screen and (max-width:800px), screen and (color) and (monochrome), screen and (max-device-width: 799px)"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("(min-width: 700px), handheld and (orientation: landscape)   ", 60),
            withExpectedResult("(color), print and (color) {", 27),
            withExpectedResult("(device-aspect-ratio: 16/9), screen", 35));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Override
    public Class<? extends Syntax> mainAstObjectClass() {
        return MediaQueryList.class;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("(color)", 3),
            withExpectedResult("(min-width:800px)", 4),
            withExpectedResult("(min-aspect-ratio: 1/1)", 6)
        ));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("all and (min-color: 4)", 1),
            withExpectedResult("all and (max-width:800px), screen and (color)", 2),
            withExpectedResult("(min-width: 700px), handheld and (orientation: landscape)", 2),
            withExpectedResult("screen and (device-aspect-ratio: 16/9), screen and (device-aspect-ratio: 16/10)", 2),
            withExpectedResult("only screen and (max-width:800px), screen and (color) and (monochrome), " +
                "screen and (max-device-width: 799px)", 3));

        for (ParseResult<Integer> result : results) {
            MediaQueryList exp = result.broadcaster.find(MediaQueryList.class).get();
            assertThat(exp.queries().size()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsOnTrailingComma() {
        exception.expect(ParserException.class);
        exception.expectMessage("trailing");
        parse("all and (max-width:800px),");
    }
}
