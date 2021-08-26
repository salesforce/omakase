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

package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.test.RespondingBroadcaster;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link FunctionValueParser}.
 *
 * @author nmcwilliams
 */
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
            "--url(one.png)",
            "-1url(one.png)");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList
            .of(
                "url(one.png)",
                "   url(one.png)",
                "/*comment*/\nurl(one.png)",
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
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("urlx(one.png)", "one.png"),
            withExpectedResult("urlx(/one/one.png)", "/one/one.png"),
            withExpectedResult("urlx(\"one.png\")", "\"one.png\""),
            withExpectedResult("urlx(\"one two three\")", "\"one two three\""),
            withExpectedResult("calc(100%/3 - 2*1em - 2*1px)", "100%/3 - 2*1em - 2*1px"),
            withExpectedResult(
                "urlx(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7"),
            withExpectedResult(
                "urlx('data:image/gif;base64," +
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
            GenericFunctionValue f = result.broadcaster.find(GenericFunctionValue.class).get();
            assertThat(f.args()).isEqualTo(result.expected);
        }
    }

    @Test
    public void matchesExpectedFunctionName() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("urlx(one.png)", "urlx"),
            withExpectedResult("calc(100%/3 - 2*1em - 2*1px)", "calc"),
            withExpectedResult(
                "urlx(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "urlx"),
            withExpectedResult("blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,)", "blahblah"),
            withExpectedResult("theme(one.\\(theme.co\\)lor)", "theme"),
            withExpectedResult("_theme(1 )", "_theme"),
            withExpectedResult("-theme__theme-t--t-11234(  )", "-theme__theme-t--t-11234"));

        for (ParseResult<String> result : results) {
            GenericFunctionValue f = result.broadcaster.find(GenericFunctionValue.class).get();
            assertThat(f.name()).isEqualTo(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        for (GenericParseResult result : parse(validSources(), false)) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(2); // raw function + specific function
        }
        
        for (GenericParseResult result : parse(validSources(), true)) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(2); // raw function + specific function
        }
    }

    @Test
    public void missingClosingParen() {  
        ParserException thrown = assertThrows(ParserException.class, () -> parse("url(afafa"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void missingClosingParenBecauseOfEscaped() {  
        ParserException thrown = assertThrows(ParserException.class, () -> parse("url(afafa\\)"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void missingClosingParenBecauseOfInString() {
        ParserException thrown = assertThrows(ParserException.class, () ->  parse("url('afafa)'"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void unclosedString() {
        ParserException thrown = assertThrows(ParserException.class, () ->  parse("url('afafafafafafafa)"));
        assertTrue(thrown.getMessage().contains("Expected to find closing"));
    }

    @Test
    public void broadcastsGenericWhenUnhandled() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        parser.parse(new Source("url(foo.png)"), new Grammar(), qb);

        Optional<GenericFunctionValue> found = qb.find(GenericFunctionValue.class);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().name()).isEqualTo("url");
        assertThat(found.get().args()).isEqualTo("foo.png");
    }

    @Test
    public void doesntBroadcastGenericWhenHandledWithFunction() {
        UrlFunctionValue url = new UrlFunctionValue("foo.png");

        RespondingBroadcaster<RawFunction> broadcaster = new RespondingBroadcaster<>(RawFunction.class, url);
        QueryableBroadcaster qb = broadcaster.chain(new QueryableBroadcaster());

        parser.parse(new Source("url(foo.png)"), new Grammar(), broadcaster);

        Optional<UrlFunctionValue> found = qb.find(UrlFunctionValue.class);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isSameAs(url);
        assertThat(qb.find(GenericFunctionValue.class).isPresent()).isFalse();
    }

    @Test
    public void doesntBroadcastGenericWhenHandledWithTerm() {
        NumericalValue nv = NumericalValue.of("12").unit("px");

        RespondingBroadcaster<RawFunction> broadcaster = new RespondingBroadcaster<>(RawFunction.class, nv);
        QueryableBroadcaster qb = broadcaster.chain(new QueryableBroadcaster());

        parser.parse(new Source("custom(a)"), new Grammar(), broadcaster);
        assertThat(qb.find(GenericFunctionValue.class).isPresent()).isFalse();
    }

    @Test
    public void changesStatusToParsedWhenHandled() {
        UrlFunctionValue url = new UrlFunctionValue("foo.png");

        RespondingBroadcaster<RawFunction> broadcaster = new RespondingBroadcaster<>(RawFunction.class, url);

        parser.parse(new Source("url(foo.png)"), new Grammar(), broadcaster);
        assertThat(broadcaster.unit().get().status()).isSameAs(Status.PARSED);
    }
}
