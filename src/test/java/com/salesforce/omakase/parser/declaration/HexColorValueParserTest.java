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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link HexColorValueParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection"})
public class HexColorValueParserTest extends AbstractParserTest<HexColorValueParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "ffffff",
            "123123",
            "fff123",
            "abc",
            "~123abc",
            "##ffffff",
            "##123123",
            "# ffffff");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "#ffffff",
            "  #ffffff",
            "/*foo*/#ffffff",
            "#132abc",
            "#999999",
            "#999",
            "#000000",
            "#987654",
            "#fefefe",
            "#FFF",
            "#F31FaB",
            " \n#F31FaB",
            "#ABCabc");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("#ffffff red", 7),
            withExpectedResult("#fff red", 4),
            withExpectedResult("#123456 #123456", 7),
            withExpectedResult("#ffeeff fff", 7),
            withExpectedResult("#abcfef red", 7),
            withExpectedResult("#defdef", 7),
            withExpectedResult("#123 _0", 4),
            withExpectedResult("#AFE !1", 4),
            withExpectedResult("#000 -00", 4));
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
            withExpectedResult("#ffffff red", "ffffff"),
            withExpectedResult("#fff red", "fff"),
            withExpectedResult("#123456 #123456", "123456"),
            withExpectedResult("#ffeeff fff", "ffeeff"),
            withExpectedResult("#abcfef red", "abcfef"),
            withExpectedResult("#defdef", "defdef"),
            withExpectedResult("#123", "123"),
            withExpectedResult("#AFE", "afe"),
            withExpectedResult("#FFFfff", "ffffff"),
            withExpectedResult("#000", "000"));

        for (ParseResult<String> result : results) {
            HexColorValue hex = expectOnly(result.broadcaster, HexColorValue.class);
            assertThat(hex.color()).isEqualTo(result.expected);
        }
    }

    @Test
    public void throwsErrorOnInvalidLength1() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#a");
    }

    @Test
    public void throwsErrorOnLength2() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#ab");
    }

    @Test
    public void throwsErrorOnLength4() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#aaaa");
    }

    @Test
    public void throwsErrorOnLength5() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#12345");
    }

    @Test
    public void throwsErrorOnLength7() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#1234567");
    }

    @Test
    public void throwsErrorOnLength8() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected a hex color of length 3 or 6");
        parse("#123456ab");
    }
}
