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
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeywordValueParser}.
 *
 * @author nmcwilliams
 */
public class KeywordValueParserTest extends AbstractParserTest<KeywordValueParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "'abc",
            "123",
            "--abs",
            "-1afafa",
            "123ABC",
            " 123",
            "-"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "abc",
            "   abc",
            "\nabc",
            "/*comment*/abc",
            "ABC",
            "AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d",
            "-abc-afakl-afa",
            "-afafma-afaf-",
            "-ADA",
            "_afjahfkahf123123",
            "a812313",
            "__afhafa_____------____-afafa",
            "_1afa",
            "a1a2f3g4_",
            "media",
            "red",
            "red11re"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("abc 123", 3),
            withExpectedResult("abc1234", 7),
            withExpectedResult("abc abc abc", 3),
            withExpectedResult("BLACK", 5),
            withExpectedResult("-afauf-afaf___afaf _af", 18),
            withExpectedResult("_1afafkslf", 10),
            withExpectedResult("afa____--___---___-afafaf---123123afafa afafa", 39),
            withExpectedResult("red 1red-1red red", 3));
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
            withExpectedResult("abc 123  ", "abc"),
            withExpectedResult("ABC__A_", "ABC__A_"),
            withExpectedResult("AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d",
                "AREALLYreallyrealllllllllllllllllllylongKeywo_wor-d"),
            withExpectedResult("_1afa ", "_1afa"),
            withExpectedResult("red11re", "red11re"),
            withExpectedResult("-ADA", "-ADA"));

        for (ParseResult<String> result : results) {
            KeywordValue value = expectOnly(result.broadcaster, KeywordValue.class);
            assertThat(value.keyword())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }
}
