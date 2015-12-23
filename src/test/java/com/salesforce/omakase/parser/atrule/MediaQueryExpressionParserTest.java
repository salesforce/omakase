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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MediaQueryExpressionParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MediaQueryExpressionParserTest extends AbstractParserTest<MediaQueryExpressionParser> {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "color",
            "!",
            "1(",
            "  @media"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "(color)",
            "   (color)",
            "(   color)",
            "(color   )",
            "(min-width:800px)",
            "(min-width: 800px)",
            "(min-width : 800px)",
            "(min-width :800px)",
            "(max-device-width: 480px)",
            "(min-color: 4)",
            "(color-index)",
            "(min-aspect-ratio: 1/1)",
            "(device-aspect-ratio: 16/9)",
            "(orientation: portrait)",
            "(min-resolution: 300dpi)",
            "(min-resolution: 2dppx)"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("(color)a", 7),
            withExpectedResult("(min-width: 700px), handheld and (orientation: landscape)", 18),
            withExpectedResult("(color), print and (color)", 7),
            withExpectedResult("(device-aspect-ratio: 16/9), screen", 27),
            withExpectedResult("( grid ) and ( max-width: 15em) ", 8));
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
            withExpectedResult("(color)", 1),
            withExpectedResult("(min-width:800px)", 1),
            withExpectedResult("(min-aspect-ratio: 1/1)", 1)
        ));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("(color)", "color"),
            withExpectedResult("   (color)", "color"),
            withExpectedResult("(min-width:800px)", "min-width"),
            withExpectedResult("(max-device-width: 480px)", "max-device-width"),
            withExpectedResult("(min-aspect-ratio: 1/1)", "min-aspect-ratio"),
            withExpectedResult("(min-resolution: 300dpi)", "min-resolution"));

        for (ParseResult<String> result : results) {
            MediaQueryExpression exp = result.broadcaster.find(MediaQueryExpression.class).get();
            assertThat(exp.feature()).isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedTerms() {
        GenericParseResult result = parse("(min-aspect-ratio: 1/1)").get(0);
        MediaQueryExpression exp = result.broadcaster.find(MediaQueryExpression.class).get();
        List<PropertyValueMember> terms = exp.terms();
        assertThat(terms).hasSize(3);
        assertThat(terms.get(0)).isInstanceOf(NumericalValue.class);
        assertThat(terms.get(1)).isInstanceOf(Operator.class);
        assertThat(terms.get(2)).isInstanceOf(NumericalValue.class);
    }

    @Test
    public void errorsIfMissingFeatureName() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_FEATURE.message());
        parse("(800px)");
    }

    @Test
    public void errorsIfMissingTerms() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_MEDIA_TERMS.message());
        parse("(max-width: )");
    }

    @Test
    public void errorsIfMissingClosingParen() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find");
        parse("(max-width: 800px");
    }
}
