/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.atrule;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link MediaQueryListParser}.
 *
 * @author nmcwilliams
 */
public class MediaQueryListParserTest extends AbstractParserTest<MediaQueryListParser> {
    @SuppressWarnings("deprecation")
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
            withExpectedResult("(min-width:800px)", 3),
            withExpectedResult("(min-aspect-ratio: 1/1)", 3)
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
