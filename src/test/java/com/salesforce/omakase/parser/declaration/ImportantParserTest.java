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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ImportantParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ImportantParserTest extends AbstractParserTest<ImportantParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "   ",
            "\n\n",
            "{}",
            "important",
            "import",
            "__",
            "a !important"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "!important",
            "!IMPORTANT",
            "  !important",
            "\n !important",
            "!imPORTant"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("!important   ", 10),
            withExpectedResult("   !important   ", 13),
            withExpectedResult("\n\n!important   ", 12),
            withExpectedResult("!important aa   ", 10),
            withExpectedResult("!important}   ", 10),
            withExpectedResult("!important;", 10));
    }

    @Override
    public String validSourceForPositionTesting() {
        return null;
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        for (GenericParseResult result : parse(validSources())) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(0);
        }
    }

    @Test
    public void throwsExceptionIfNoImportantAfterBang() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_IMPORTANT.message());
        parse("!import");
    }

    @Test
    public void throwsExceptionIfNoImportantAfterBangPart2() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_IMPORTANT.message());
        parse("!hithere");
    }

    @Test
    public void throwsExceptionIfSpaceAfterBang() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_IMPORTANT.message());
        parse("! important");
    }

    @Override
    public void matchesExpectedBroadcastContent() {
        //n/a
    }
}
