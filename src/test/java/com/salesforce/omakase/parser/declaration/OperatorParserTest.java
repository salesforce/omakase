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
