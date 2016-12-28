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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
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
        exception.expectMessage(Message.EXPECTED_IMPORTANT);
        parse("!import");
    }

    @Test
    public void throwsExceptionIfNoImportantAfterBangPart2() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_IMPORTANT);
        parse("!hithere");
    }

    @Test
    public void throwsExceptionIfSpaceAfterBang() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_IMPORTANT);
        parse("! important");
    }

    @Override
    public void matchesExpectedBroadcastContent() {
        //n/a
    }
}
