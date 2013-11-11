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

package com.salesforce.omakase.parser.selector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.selector.UniversalSelector;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;

/**
 * Unit tests for {@link UniversalSelectorParser}.
 *
 * @author nmcwilliams
 */
public class UniversalSelectorParserTest extends AbstractParserTest<UniversalSelectorParser> {
    @Override
    public List<String> invalidSources() {
        return Lists.newArrayList(
            ".class",
            "#id",
            " *",
            "div*");
    }

    @Override
    public List<String> validSources() {
        return Lists.newArrayList("*");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("*#div", 1),
            withExpectedResult("*.class", 1),
            withExpectedResult("* class", 1));
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
        List<GenericParseResult> results = parse("*");
        results.get(0).broadcaster.findOnly(UniversalSelector.class).get();
    }
}
