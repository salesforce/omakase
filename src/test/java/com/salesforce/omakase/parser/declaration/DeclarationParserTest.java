/*
 * Copyright (c) 2017, salesforce.com, inc.
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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.test.RespondingBroadcaster;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link DeclarationParser}.
 *
 * @author nmcwilliams
 */
public class DeclarationParserTest extends AbstractParserTest<DeclarationParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "   ",
            "\n",
            "{color: red}",
            "^",
            "$name",
            "- - ");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList
            .of(
                "color:red",
                "/*comment*//*comment*/ color: red",
                "/*comment*/\ncolor:red",
                "-moz-border-radius: 5px",
                "-webkit-border-radius: 10px",
                "border-radius: 10px",
                "background: url(one.png)",
                "background:url(/one/one.png) !important",
                "background: url('one/one.png')",
                "background: url(\"one.png\")",
                "background: url(\"one two three\")",
                "background: url(one two three)",
                "background: calc(100%/3 - 2*1em - 2*1px)",
                "background: calc(50% + 20px)",
                "background: toggle(disc, circle, square, box)",
                "background: attr(length em)",
                "background: url(data:image/gif;base64," +
                    "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7)",
                "background: url('data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                "background: repeating-linear-gradient(90deg, transparent, transparent 50px,\n      rgba(255, 127, 0, " +
                    "0.25) 50px, rgba(255, 127, 0, 0.25) 56px, transparent 56px, transparent 63px,\n      rgba(255, 127, " +
                    "0, 0.25) 63px, rgba(255, 127, 0, 0.25) 69px, transparent 69px, transparent 116px,\n      rgba(255, " +
                    "206, 0, 0.25) 116px, rgba(255, 206, 0, 0.25) 166px)",
                "background: hsla(0,100%,50%,0.4)",
                "background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))",
                "background: rotateX(80deg)",
                "background: linear-gradient(45deg,rgba(0,0,0,0.24) 0%,rgba(0,0,0,0) 100%)",
                "background: radial-gradient(red, yellow, rgb(30, 144, 255))",
                "background: radial-gradient(red 5%, yellow 25%, #1E90FF 50%)",
                "background: blahblah(1,1,1,2$*_918930939, , , , ,    ,, ~-``9289,)",
                "background: theme(one.theme.color)",
                "background: theme(one.\\(theme.co\\)lor)",
                "background: _theme(1 )",
                "background: -theme-theme(1)",
                "margin: 1",
                "  margin: 0",
                "margin:1px !important",
                "margin: 1.1px",
                "margin: 1.1",
                "margin: 12345678910",
                "margin: 123456713131890.1234567713188912",
                "margin: 123456713131890.1234567713188912px",
                "margin: 0.1234567713188912px",
                "margin: 0.1",
                "margin: .1",
                "margin: 1em",
                "margin: 1234deg",
                "margin: -1px",
                "margin: -1",
                "margin: 10%",
                "margin: 1.1%",
                "margin: +1px",
                "margin: +1.1em",
                "font-family:\"Times new Roman\"",
                "--test:red",
                "--my-color: 222, 202, 180",
                "--MY_SIZE: 14rem",
                "--somecomplexvalue: 5px 200px rgb(20, 32, 54)",
                "--my-alias: var(--my-color, var(--my-other-color, red))"
            );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList
            .of(
                withExpectedResult("color:red", 9),
                withExpectedResult("/*comment*//*comment*/ color: red", 33),
                withExpectedResult("background:url(/one/one.png)", 28),
                withExpectedResult("background: calc(100%/3 - 2*1em - 2*1px); color:red", 40),
                withExpectedResult(
                    "background: url('data:image/gif;base64," +
                        "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                        "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                    330),
                withExpectedResult("background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))", 80),
                withExpectedResult("font-family: \":;test}\", Arial", 29),
                withExpectedResult("color:red;margin:10px", 9),
                withExpectedResult("display:none;\n\n  position:absolute", 12),
                withExpectedResult("\n color: red} \n .class2 {color: red}", 12),
                withExpectedResult("color: /*comment\ncomment*/ blue; ", 31),
                withExpectedResult("*color:red", 10),
                withExpectedResult("/*comment*//*comment*/ *color: red", 34),
                withExpectedResult("--my-alias: var(--my-color, var(--my-other-color, red)); color: red", 55)
            );
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
            withExpectedResult("color:red", "red"),
            withExpectedResult("/*comment*//*comment*/ color: red", "red"),
            withExpectedResult("margin: calc(100%/3 - 2*1em - 2*1px); color:red", "calc(100%/3 - 2*1em - 2*1px)"),
            withExpectedResult(
                "background: url('data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')",
                "url('data:image/gif;base64," +
                    "R0lGOD(lhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7" +
                    "////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQAosJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7')"),
            withExpectedResult(
                "background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))",
                "-webkit-gradient(linear, 0% 0%, 0% 100%, from(#7F8792), to(#535B68))"),
            withExpectedResult("font-family: \":;test}\", Arial", "\":;test}\", Arial"),
            withExpectedResult("color:red;margin:10px", "red"),
            withExpectedResult("display:none;\n\n  position:absolute", "none"),
            withExpectedResult("color: /*comment\ncomment*/ blue; ", "/*comment\ncomment*/ blue"),
            withExpectedResult("color:blue /*comment*/; ", "blue /*comment*/"),
            withExpectedResult("color:blue !important; ", "blue !important"),
            withExpectedResult("color:blue !important", "blue !important"));

        for (ParseResult<String> result : results) {
            Declaration d = expectOnly(result.broadcaster, Declaration.class);
            assertThat(d.rawPropertyValue().get().content()).isEqualTo(result.expected);
        }
    }

    @Test
    public void correctPropertyName() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("color:red", "color"),
            withExpectedResult("border-radius: 5px", "border-radius"),
            withExpectedResult("border-radius:5px", "border-radius"),
            withExpectedResult("border-radius :5px", "border-radius"),
            withExpectedResult("-moz-border-radius: 5px", "-moz-border-radius"),
            withExpectedResult("-ms-sucks: 100%", "-ms-sucks"),
            withExpectedResult("display:none", "display"),
            withExpectedResult("COLOR:red", "COLOR"),
            withExpectedResult("*COLOR:red", "*COLOR"),
            withExpectedResult("ANIMATION-timing-function:red", "ANIMATION-timing-function"),
            withExpectedResult("--my-color: 222, 202, 180", "--my-color"),
            withExpectedResult("--MY-COLOR: blue", "--MY-COLOR"));

        for (ParseResult<String> result : results) {
            Declaration d = expectOnly(result.broadcaster, Declaration.class);
            assertThat(d.rawPropertyName().get().content()).isEqualTo(result.expected);
        }
    }

    @Test
    public void missingColon() {
        ParserException thrown = assertThrows(ParserException.class, () -> parse("color red"));
        assertTrue(thrown.getMessage().contains(Message.MISSING_COLON));
    }

    @Test
    public void attachesCommentsBeforeProperty() {
        GenericParseResult result = parse("/*comment*/color: red").get(0);
        Declaration d = expectOnly(result.broadcaster, Declaration.class);
        assertThat(d.comments()).hasSize(1);
    }

    @Test
    public void attachesPropertyValue() {
        NumericalValue nv = NumericalValue.of(12).unit("px");
        PropertyValue pv = PropertyValue.of(nv);

        RespondingBroadcaster<Declaration> broadcaster = new RespondingBroadcaster<>(Declaration.class, nv, pv);

        parser.parse(new Source("margin: 12px"), new Grammar(), broadcaster);

        Declaration declaration = broadcaster.unit().get();
        assertThat(declaration.propertyValue().members()).hasSize(1);
        assertThat(declaration.propertyValue()).isSameAs(pv);
    }

    @Test
    public void noPropertyValueToAttach() {
        RespondingBroadcaster<Declaration> broadcaster = new RespondingBroadcaster<>(Declaration.class);
        parser.parse(new Source("margin: 12px"), new Grammar(), broadcaster);

        Optional<Declaration> declaration = broadcaster.unit();
        assertThat(declaration.get().propertyValue().members()).isEmpty();
    }
}
