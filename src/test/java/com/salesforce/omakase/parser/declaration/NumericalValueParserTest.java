/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import static com.salesforce.omakase.util.Templates.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue.Sign;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.util.Templates.SourceWithExpectedResult;

/**
 * Unit tests for {@link NumericalValueParser}.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("javadoc")
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
            "0.1",
            ".1",
            "1em",
            "1234deg",
            "-1px",
            "-1",
            "10%",
            "1.1%",
            "+1px",
            "+1.1em");
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
    public boolean allowedToTrimLeadingWhitespace() {
        return false;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("1", 1),
            withExpectedResult("0", 0),
            withExpectedResult("1px", 1),
            withExpectedResult("1.1px", 1),
            withExpectedResult("1234567", 1234567),
            withExpectedResult("1234567131.1234567713188912", 1234567131),
            withExpectedResult("0.1234567713188912px", 0),
            withExpectedResult("0.1", 0),
            withExpectedResult(".1", 0));

        for (ParseResult<Integer> result : results) {
            NumericalValue n = result.broadcaster.findOnly(NumericalValue.class).get();
            assertThat(n.integerValue()).isEqualTo(result.expected);
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
    public void decimalAbsent() {
        List<GenericParseResult> result = parse("1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.decimalValue().isPresent()).isFalse();
    }

    @Test
    public void decimalPresent() {
        List<GenericParseResult> result = parse("1.55");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.decimalValue().get()).isEqualTo(55);
    }

    @Test
    public void integerValueAbsent() {
        List<GenericParseResult> result = parse(".1");
        NumericalValue n = result.get(0).broadcaster.findOnly(NumericalValue.class).get();
        assertThat(n.integerValue()).isEqualTo(0);
    }

    @Test
    public void noNumberAfterDecimal() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.EXPECTED_DECIMAL.message());
        parse("1.");
    }

}
