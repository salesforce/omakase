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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.NumericalValue.Sign;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;
import org.junit.Test;

import java.util.List;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link NumericalValueParser}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class NumericalValueParserTest extends AbstractParserTest<NumericalValueParser> {

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "   1",
            "\n1",
            "abc",
            "px",
            "abc.1234",
            "-",
            "\"1\"",
            "__",
            "- 1",
            "-anc");
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "1",
            "0",
            "1px",
            "1.1px",
            "1.1",
            "12345678910",
            "123456713131890.1234567713188912",
            "123456713131890.1234567713188912px",
            "0.1234567713188912px",
            "1.0000011",
            "0.1",
            ".1",
            "1em",
            "1234deg",
            "-1px",
            "-1",
            "10%",
            "1.1%",
            "+1px",
            "+1.1em",
            "-.8em");
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("1", 1),
            withExpectedResult("01", 2),
            withExpectedResult("1px", 3),
            withExpectedResult("1 px", 1),
            withExpectedResult("+1 .1em ", 2),
            withExpectedResult("0.1em", 5),
            withExpectedResult("123456713131890.1234567713188912px", 34),
            withExpectedResult("123323123120001 11", 15),
            withExpectedResult("-1px -", 4),
            withExpectedResult("1px red", 3),
            withExpectedResult("1px\nred", 3));
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
    @SuppressWarnings("unchecked")
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<Double>> results = parseWithExpected(
            withExpectedResult("1", 1d),
            withExpectedResult("0", 0d),
            withExpectedResult("1px", 1d),
            withExpectedResult("1.1px", 1.1),
            withExpectedResult("1234567", 1234567d),
            withExpectedResult("1234567131.1234567713188912", 1234567131.1234567713188912),
            withExpectedResult("0.1234567713188912px", 0.1234567713188912),
            withExpectedResult("0.1", 0.1),
            withExpectedResult(".1", 0.1),
            withExpectedResult("1.001", 1.001)
        );

        for (ParseResult<Double> result : results) {
            NumericalValue n = result.broadcaster.findOnly(NumericalValue.class).get();
            assertThat(n.doubleValue()).isEqualTo(result.expected);
        }
    }

    @Test
    public void explicitSignAbsent() {
        List<GenericParseResult> result = parse("1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.explicitSign().isPresent()).isFalse();
    }

    @Test
    public void explicitSignPositive() {
        List<GenericParseResult> result = parse("+1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.explicitSign().get()).isEqualTo(Sign.POSITIVE);
    }

    @Test
    public void explicitSignNegative() {
        List<GenericParseResult> result = parse("-1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.explicitSign().get()).isEqualTo(Sign.NEGATIVE);
    }

    @Test
    public void unitAbsent() {
        List<GenericParseResult> result = parse("1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.unit().isPresent()).isFalse();
    }

    @Test
    public void unitPresent() {
        List<GenericParseResult> result = parse("1px");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.unit().get()).isEqualTo("px");
    }

    @Test
    public void noNumberAfterDecimal() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_DECIMAL.message());
        parse("1.");
    }
}
