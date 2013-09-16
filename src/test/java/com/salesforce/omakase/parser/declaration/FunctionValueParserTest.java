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

package com.salesforce.omakase.parser.declaration;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.Templates.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link FunctionValueParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc"})
public class FunctionValueParserTest extends AbstractParserTest<FunctionValueParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "$url(one.png)",
            "1url(one.png)",
            "url",
            "rgba",
            "rgba1 (",
            "rgba\n(1px, 1px, 1px, 1%)",
            "  url(one.png)",
            "--url(one.png)",
            "-1url(one.png)");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList
            .of(
                "url(one.png)",
                "url(/one/one.png)",
                "url('one/one.png')",
                "url(\"one.png\")",
                "url(\"one two three\")",
                "url(one two three)",
                "calc(100%/3 - 2*1em - 2*1px)",
                "calc(50% + 20px)",
                "toggle(disc, circle, square, box)",
                "attr(length em)",
                "url(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "url('data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                "repeating-linear-gradient(90deg, transparent, transparent 50px,\n      rgba(255, 127, 0, 0.25) 50px, " +
                    "rgba(255, 127, 0, 0.25) 56px, transparent 56px, transparent 63px,\n      rgba(255, 127, 0, " +
                    "0.25) 63px, rgba(255, 127, 0, 0.25) 69px, transparent 69px, transparent 116px,\n      rgba(255, 206, " +
                    "0, 0.25) 116px, rgba(255, 206, 0, 0.25) 166px)",
                "hsla(0,100%,50%,0.4)",
                "-webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))",
                "rotateX(80deg)",
                "linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%)",
                "radial-gradient(red, yellow, rgb(30, 144, 255))",
                "radial-gradient(red 5%, yellow 25%, #1E90FF 50%)",
                "blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,)",
                "theme(one.theme.color)",
                "theme(one.\\(theme.co\\)lor)",
                "_theme(1 )",
                "-theme-theme(1)",
                "linear-gradient(45deg,/*x*/rgba(0,0,0,0.24) 0%,/*)*/rgba(0,0,0,0) 100%)"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList
            .of(
                withExpectedResult("url(/one.png) url(/one.png) 5px", 13),
                withExpectedResult("url(/one/one.png)    ", 17),
                withExpectedResult("url(one two three)_", 18),
                withExpectedResult("calc(50% + 20px ))", 17),
                withExpectedResult(
                    "url(data:image/gif;base64," +
                        "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                        "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                    315),
                withExpectedResult(
                    "url('data:image/gif;base64," +
                        "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                        "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                    318),
                withExpectedResult("blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289)", 53),
                withExpectedResult("abc()", 5),
                withExpectedResult("-one-two-three--four__abc(1)", 28));
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("url(one.png)", "one.png"),
            withExpectedResult("url(/one/one.png)", "/one/one.png"),
            withExpectedResult("url(\"one.png\")", "\"one.png\""),
            withExpectedResult("url(\"one two three\")", "\"one two three\""),
            withExpectedResult("calc(100%/3 - 2*1em - 2*1px)", "100%/3 - 2*1em - 2*1px"),
            withExpectedResult(
                "url(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7"),
            withExpectedResult(
                "url('data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                "'data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7'"),
            withExpectedResult(
                "blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,)",
                "1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,"),
            withExpectedResult("theme(one.\\(theme.co\\)lor)", "one.\\(theme.co\\)lor"),
            withExpectedResult("_theme(1 )", "1 "));

        for (ParseResult<String> result : results) {
            FunctionValue f = result.broadcaster.findOnly(FunctionValue.class).get();
            assertThat(f.args()).isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedFunctionName() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("url(one.png)", "url"),
            withExpectedResult("calc(100%/3 - 2*1em - 2*1px)", "calc"),
            withExpectedResult(
                "url(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "url"),
            withExpectedResult("blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,)", "blahblah"),
            withExpectedResult("theme(one.\\(theme.co\\)lor)", "theme"),
            withExpectedResult("_theme(1 )", "_theme"),
            withExpectedResult("-theme__theme-t--t-11234(  )", "-theme__theme-t--t-11234"));

        for (ParseResult<String> result : results) {
            FunctionValue f = result.broadcaster.findOnly(FunctionValue.class).get();
            assertThat(f.name()).isEqualTo(result.expected);
        }
    }

    @Test
    public void missingClosingParen() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("url(afafa");
    }

    @Test
    public void missingClosingParenBecauseOfEscaped() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("url(afafa\\)");
    }

    @Test
    public void missingClosingParenBecauseOfInString() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("url('afafa)'");
    }

    @Test
    public void unclosedString() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find closing");
        parse("url('afafafafafafafa)");
    }
}
