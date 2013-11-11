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
            "  afafk",
            " 123",
            "-"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "abc",
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
        return false;
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
            KeywordValue value = result.broadcaster.findOnly(KeywordValue.class).get();
            assertThat(value.keyword())
                .describedAs(result.source.toString())
                .isEqualTo(result.expected);
        }
    }
}
