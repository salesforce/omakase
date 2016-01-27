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

    @Test
    public void changeValueFromPositiveToPositive() {
        numerical = NumericalValue.of(5);
        numerical.value(numerical.intValue() + 1);
        assertThat(numerical.value()).isEqualTo("6");
        assertThat(numerical.intValue()).isEqualTo(6);
        assertThat(numerical.explicitSign().isPresent()).isFalse();
    }

    @Test
    public void changeValueFromPositiveToNegative() {
        numerical = NumericalValue.of(5);
        numerical.value(-1);
        assertThat(numerical.value()).isEqualTo("1");
        assertThat(numerical.intValue()).isEqualTo(-1);
        assertThat(numerical.doubleValue()).isEqualTo(-1.0);
        assertThat(numerical.explicitSign().get()).isEqualTo(NumericalValue.Sign.NEGATIVE);
    }

    @Test
    public void changeValueFromNegativeToNegative() {
        numerical = NumericalValue.of(-5.5);
        numerical.value(numerical.doubleValue() + 1);
        assertThat(numerical.value()).isEqualTo("4.5");
        assertThat(numerical.doubleValue()).isEqualTo(-4.5);
        assertThat(numerical.explicitSign().get()).isEqualTo(NumericalValue.Sign.NEGATIVE);
    }

    @Test
    public void changeValueFromNegativeToPositive() {
        numerical = NumericalValue.of(-10);
        numerical.value(12);
        assertThat(numerical.value()).isEqualTo("12");
        assertThat(numerical.intValue()).isEqualTo(12);
        assertThat(numerical.explicitSign().isPresent()).isFalse();
    }

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
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10");
    }

    @Test
    public void writeWithIntegerAndUnit() throws IOException {
        numerical = NumericalValue.of(10, "px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10px");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10px");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10px");
    }

    @Test
    public void writeWithIntegerAndDecimal() throws IOException {
        numerical = new NumericalValue(1, 1, "5.5");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("5.5");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("5.5");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("5.5");
    }

    @Test
    public void writeWithIntegerDecimcalAndUnit() throws IOException {
        numerical = NumericalValue.of(10.3).unit("em");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10.3em");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10.3em");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10.3em");
    }

    @Test
    public void writeWithIntegerDecimcalAndUnitFromString() throws IOException {
        numerical = NumericalValue.of("10.3", "em");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10.3em");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10.3em");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10.3em");
    }

    @Test
    public void writeWithNegativeSign() throws IOException {
        numerical = NumericalValue.of(10).explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("-10");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("-10");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("-10");
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
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasDecimalAndLeadingZero() throws IOException {
        numerical = NumericalValue.of(0.4);
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.4");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo(".4");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo(".4");
    }

    @Test
    public void writeHasDecimalAndLeadingZeroFromString() throws IOException {
        numerical = new NumericalValue(1, 1, "0.4");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.4");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo(".4");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo(".4");
    }

    @Test
    public void writeHasDecimalAndUnitAndLeadingZeroValue() throws IOException {
        numerical = NumericalValue.of(0.4).unit("rem");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.4rem");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo(".4rem");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo(".4rem");
    }

    @Test
    public void writeHasMultipleLeadingZeros() throws Exception {
        numerical = new NumericalValue(1, 1, "0000.4");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0000.4");
        // this is not so much deliberate as just acceptable
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0000.4");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0000.4");
    }

    @Test
    public void writeHasLeadingZeroAndDecimalZero() throws Exception {
        numerical = new NumericalValue(1, 1, "0.0");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.0");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasLeadingZeroAndMultipleDecimalZeros() throws Exception {
        numerical = new NumericalValue(1, 1, "0.0000");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.0000");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasLeadingZeroAndDecimalZeroAndUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "0.0").unit("px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0.0px");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasMultipleZeros() throws Exception {
        numerical = new NumericalValue(1, 1, "0000");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0000");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasMultipleZerosAndUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "0000").unit("rem");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0000rem");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasMultipleZerosEmptyLeading() throws Exception {
        numerical = new NumericalValue(1, 1, ".00");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo(".00");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeHasSingleZeroAndUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "0").unit("px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0px");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0");
    }

    @Test
    public void writeZeroWithTimeUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "0").unit("s");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0s");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0s");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0s");
    }

    @Test
    public void writeZeroWithAngleUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "00").unit("deg");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("00deg");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0deg");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0deg");
    }

    @Test
    public void writeZeroWithUnknownUnit() throws Exception {
        numerical = new NumericalValue(1, 1, "0").unit("xxx");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("0xxx");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("0xxx");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("0xxx");
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
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10000000000.1");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10000000000.1");
    }

    @Test
    public void writeLargeValueFromStringTrailingZeroes() {
        numerical = NumericalValue.of("10000000000.100000000000");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("10000000000.100000000000");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("10000000000.100000000000");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("10000000000.100000000000");
    }

    @Test
    public void writeValueWithLeadingZeroInDecimal() {
        numerical = NumericalValue.of(1.083, "px");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("1.083px");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("1.083px");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("1.083px");
    }

    @Test
    public void writeValueWithLeadingZeroInDecimalFromString() {
        numerical = new NumericalValue(1, 1, "1.083");
        assertThat(StyleWriter.verbose().writeSnippet(numerical)).isEqualTo("1.083");
        assertThat(StyleWriter.inline().writeSnippet(numerical)).isEqualTo("1.083");
        assertThat(StyleWriter.compressed().writeSnippet(numerical)).isEqualTo("1.083");
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
