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
import com.salesforce.omakase.ast.atrule.MediaQueryExpression;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.TermListMember;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.salesforce.omakase.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.*;

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
            withExpectedResult("(min-width:800px)", 2),
            withExpectedResult("(min-aspect-ratio: 1/1)", 4)
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
        List<TermListMember> terms = exp.terms();
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
