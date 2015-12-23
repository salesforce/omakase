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
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.parser.AbstractParserTest;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link OperatorParser}.
 *
 * @author nmcwilliams
 */
public class OperatorParserTest extends AbstractParserTest<OperatorParser> {
    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "1",
            "abc",
            ":",
            "~"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            " ",
            ",",
            "/",
            " ,",
            " /",
            ", ",
            "/ ",
            " , ",
            " / ",
            "  "
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult(" a", 1),
            withExpectedResult(",,", 1),
            withExpectedResult("    ", 4),
            withExpectedResult(", a", 2),
            withExpectedResult(" ,a", 2),
            withExpectedResult(" / a", 3),
            withExpectedResult("/*x*/  ", 7),
            withExpectedResult("  /*x*/ , 1px", 10),
            withExpectedResult("/*x*/ /*x*//,", 12));
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
        List<ParseResult<OperatorType>> results = parseWithExpected(
            withExpectedResult(" ", OperatorType.SPACE),
            withExpectedResult("    ", OperatorType.SPACE),
            withExpectedResult("  ,", OperatorType.COMMA),
            withExpectedResult("  , ", OperatorType.COMMA),
            withExpectedResult("/", OperatorType.SLASH),
            withExpectedResult(",/", OperatorType.COMMA),
            withExpectedResult(" /,", OperatorType.SLASH),
            withExpectedResult("/*x*/  ", OperatorType.SPACE),
            withExpectedResult("  /*x*/ , 1px", OperatorType.COMMA),
            withExpectedResult("/*x*/ /*x*//", OperatorType.SLASH),
            withExpectedResult(",   ", OperatorType.COMMA)
        );

        for (ParseResult<OperatorType> result : results) {
            Operator o = result.broadcaster.findOnly(Operator.class).get();
            assertThat(o.type()).isSameAs(result.expected);
        }
    }
}
