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

package com.salesforce.omakase.ast.declaration;

import com.google.common.collect.Lists;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link NumericalValue}. */
@SuppressWarnings("JavaDoc")
public class NumericalValueTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private NumericalValue numerical;

    @Test
    public void valueFromString() {
        numerical = new NumericalValue(5, 5, "100");
        assertThat(numerical.value()).isEqualTo("100");
    }

    @Test
    public void valueFromStringAsDouble() {
        numerical = new NumericalValue(5, 5, "100");
        assertThat(numerical.doubleValue()).isEqualTo(100);
    }

    @Test
    public void aLongValue() {
        numerical = NumericalValue.of("100000000000000");
        assertThat(numerical.value()).isEqualTo("100000000000000");
    }

    @Test
    public void aLongValueAsDouble() {
        numerical = NumericalValue.of("100000000000000");
        assertThat(numerical.doubleValue()).isEqualTo(100000000000000d);
    }

    @Test
    public void aLongValueAsDoubleWithFloatingPoint() {
        numerical = NumericalValue.of("10000000.555");
        assertThat(numerical.doubleValue()).isEqualTo(10000000.555);
    }

    @Test
    public void valueFromStringLeadingZero() {
        numerical = new NumericalValue(5, 5, "000.100");
        assertThat(numerical.value()).isEqualTo("000.100");
    }

    @Test
    public void setValueFromInteger() {
        numerical = new NumericalValue(1);
        numerical.value(100);
        assertThat(numerical.value()).isEqualTo("100");
        assertThat(numerical.doubleValue()).isEqualTo(100);
    }

    @Test
    public void setValueFromDouble() {
        numerical = new NumericalValue(5);
        numerical.value(10.5);
        assertThat(numerical.value()).isEqualTo("10.5");
        assertThat(numerical.doubleValue()).isEqualTo(10.5);
    }

    @Test
    public void setValueFromDoubleLeadingZero() {
        numerical = new NumericalValue(5);
        numerical.value(0.5);
        assertThat(numerical.value()).isEqualTo("0.5");
        assertThat(numerical.doubleValue()).isEqualTo(0.5);
    }

    @Test
    public void setValueFromDoubleManyLeadingZero() {
        numerical = new NumericalValue(5);
        numerical.value(000.5);
        assertThat(numerical.value()).isEqualTo("0.5");
    }

//    @Test
//    public void errorsOnNegativeIntegerValue() {
//        exception.expect(IllegalArgumentException.class);
//        numerical = new NumericalValue(-5);
//    }
//
//    @Test
//    public void errorsOnNegativeDoubleValue() {
//        exception.expect(IllegalArgumentException.class);
//        numerical = new NumericalValue(-5.5);
//    }

    @Test
    public void getDoubleValue() {
        numerical = new NumericalValue(5.5);
        assertThat(numerical.doubleValue()).isEqualTo(5.5);
    }

    @Test
    public void getDoubleValueWhenNegative() {
        numerical = new NumericalValue(5.5);
        numerical.explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(numerical.doubleValue()).isEqualTo(-5.5);
    }

    @Test
    public void getIntegerValue() {
        numerical = new NumericalValue(5);
        assertThat(numerical.intValue()).isEqualTo(5);
    }

    @Test
    public void getIntegerValueWhenNegative() {
        numerical = new NumericalValue(5);
        numerical.explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(numerical.intValue()).isEqualTo(-5);
    }

    @Test
    public void setUnit() {
        numerical = new NumericalValue(5);
        numerical.unit("px");
        assertThat(numerical.unit().get()).isEqualTo("px");
    }

    @Test
    public void setExplicitSign() {
        numerical = new NumericalValue(5);
        numerical.explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(numerical.explicitSign().get()).isSameAs(NumericalValue.Sign.NEGATIVE);
    }

    @Test
    public void isNegativeTrue() {
        numerical = new NumericalValue(5);
        numerical.explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(numerical.isNegative()).isTrue();
    }

    @Test
    public void isNegativeFalse() {
        numerical = new NumericalValue(5);
        assertThat(numerical.isNegative()).isFalse();
        numerical.explicitSign(NumericalValue.Sign.POSITIVE);
        assertThat(numerical.isNegative()).isFalse();
    }

    @Test
    public void textualValueInteger() {
        numerical = NumericalValue.of(10);
        assertThat(numerical.textualValue()).isEqualTo("10");
    }

    @Test
    public void textualValueDouble() {
        numerical = NumericalValue.of(10.5);
        assertThat(numerical.textualValue()).isEqualTo("10.5");
    }

    @Test
    public void textualValueLeadingZero() {
        numerical = NumericalValue.of(0.9);
        assertThat(numerical.textualValue()).isEqualTo("0.9");
    }

    @Test
    public void textualValueNumberAndSign() {
        numerical = NumericalValue.of(10).explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(numerical.textualValue()).isEqualTo("-10");
    }

    @Test
    public void textualValueNumberAndUnit() {
        numerical = NumericalValue.of(10).unit("px");
        assertThat(numerical.textualValue()).isEqualTo("10px");
    }

    @Test
    public void textualValueNumberSignAndUnit() {
        numerical = NumericalValue.of(10)
            .unit("px")
            .explicitSign(NumericalValue.Sign.NEGATIVE);

        assertThat(numerical.textualValue()).isEqualTo("-10px");
    }

    @Test
    public void writeWithIntegerOnly() throws IOException {
        numerical = NumericalValue.of(10);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10");
    }

    @Test
    public void writeWithIntegerAndUnit() throws IOException {
        numerical = NumericalValue.of(10, "px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10px");
    }

    @Test
    public void writeWithIntegerAndDecimal() throws IOException {
        numerical = new NumericalValue(1, 1, "5.5");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("5.5");
    }

    @Test
    public void writeWithIntegerDecimcalAndUnit() throws IOException {
        numerical = NumericalValue.of(10.3).unit("em");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10.3em");
    }

    @Test
    public void writeWithIntegerDecimcalAndUnitFromString() throws IOException {
        numerical = NumericalValue.of("10.3", "em");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10.3em");
    }

    @Test
    public void writeWithNegativeSign() throws IOException {
        numerical = NumericalValue.of(10).explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("-10");
    }

    @Test
    public void writeWithPositiveSign() throws IOException {
        numerical = NumericalValue.of(10.1, "px").explicitSign(NumericalValue.Sign.POSITIVE);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("+10.1px");
    }

    @Test
    public void writeIntegerOnlyAndZeroValue() throws IOException {
        numerical = NumericalValue.of(0);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasDecimalAndIntegerHasZeroValue() throws IOException {
        numerical = NumericalValue.of(0.4);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo(".4");
    }

    @Test
    public void writeHasDecimalAndIntegerHasZeroValueFromString() throws IOException {
        numerical = new NumericalValue(1, 1, "0.4");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo(".4");
    }

    @Test
    public void writeWithDecimalAndUnitAndIntegerHasZeroValue() throws IOException {
        numerical = NumericalValue.of(0.4).unit("rem");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo(".4rem");
    }

    @Test
    public void writeLargeValue() {
        numerical = NumericalValue.of("3000000.100000000009");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("3000000.100000000009");
    }

    @Test
    public void writeLargeValueFromDoubleTrailingZeroes() {
        numerical = NumericalValue.of(10000000000.100000000000);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10000000000.1");
    }

    @Test
    public void writeLargeValueFromStringTrailingZeroes() {
        numerical = NumericalValue.of("10000000000.100000000000");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10000000000.100000000000");
    }

    @Test
    public void writeValueWithLeadingZeroInDecimal() {
        numerical = NumericalValue.of(1.083, "px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("1.083px");
    }

    @Test
    public void writeValueWithLeadingZeroInDecimalFromString() {
        numerical = new NumericalValue(1, 1, "1.083");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("1.083");
    }

    @Test
    public void copyTest() {
        numerical = new NumericalValue(5, 5, "100");
        numerical.unit("px");
        numerical.explicitSign(NumericalValue.Sign.NEGATIVE);
        numerical.comments(Lists.newArrayList("test"));

        NumericalValue copy = numerical.copy();
        assertThat(copy.value()).isEqualTo(numerical.value());
        assertThat(copy.unit().get()).isEqualTo(numerical.unit().get());
        assertThat(copy.explicitSign().get()).isSameAs(numerical.explicitSign().get());
        assertThat(copy.comments()).hasSameSizeAs(numerical.comments());
    }

    @Test
    public void copyTestWithAbsentValues() {
        numerical = new NumericalValue(5, 5, "100");

        NumericalValue copy = numerical.copy();
        assertThat(copy.value()).isEqualTo(numerical.value());
        assertThat(copy.unit().isPresent()).isFalse();
        assertThat(copy.explicitSign().isPresent()).isFalse();
    }
}
