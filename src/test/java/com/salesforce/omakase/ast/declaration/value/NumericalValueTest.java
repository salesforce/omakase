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

package com.salesforce.omakase.ast.declaration.value;

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link NumericalValue}. */
@SuppressWarnings("JavaDoc")
public class NumericalValueTest {
    private NumericalValue value;

    @Test
    public void integerValueOnly() {
        value = new NumericalValue(5, 5, 100);
        assertThat(value.integerValue()).isEqualTo(100);
    }

    @Test
    public void integerValueOnlyUsingLong() {
        value = new NumericalValue(5, 5, 100l);
        assertThat(value.integerValue()).isEqualTo(100);
    }

    @Test
    public void setIntegerValue() {
        value = new NumericalValue(1);
        value.integerValue(100);
        assertThat(value.integerValue()).isEqualTo(100);
    }

    @Test
    public void setIntegerValueUsingLong() {
        value = new NumericalValue(1);
        value.integerValue(100l);
        assertThat(value.integerValue()).isEqualTo(100);
    }

    @Test
    public void setDecimalValue() {
        value = new NumericalValue(5);
        value.decimalValue(10);
        assertThat(value.decimalValue().get()).isEqualTo(10);
    }

    @Test
    public void setUnit() {
        value = new NumericalValue(5);
        value.unit("px");
        assertThat(value.unit().get()).isEqualTo("px");
    }

    @Test
    public void setExplicitSign() {
        value = new NumericalValue(5);
        value.explicitSign(NumericalValue.Sign.NEGATIVE);
        assertThat(value.explicitSign().get()).isSameAs(NumericalValue.Sign.NEGATIVE);
    }

    @Test
    public void writeWithIntegerOnly() throws IOException {
        value = NumericalValue.of(10);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("10");
    }

    @Test
    public void writeWithIntegerAndUnit() throws IOException {
        value = NumericalValue.of(10, "px");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("10px");
    }

    @Test
    public void writeWithIntegerAndDecimal() throws IOException {
        value = new NumericalValue(5l).decimalValue(5);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("5.5");
    }

    @Test
    public void writeWithIntegerDecimcalAndUnit() throws IOException {
        value = NumericalValue.of(10).decimalValue(3).unit("em");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("10.3em");
    }

    @Test
    public void writeWithNegativeSign() throws IOException {
        value = NumericalValue.of(10).explicitSign(NumericalValue.Sign.NEGATIVE);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("-10");
    }

    @Test
    public void writeWIthPositiveSign() throws IOException {
        value = NumericalValue.of(10, "px").decimalValue(1).explicitSign(NumericalValue.Sign.POSITIVE);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("+10.1px");
    }

    @Test
    public void writeIntegerOnlyAndZeroValue() throws IOException {
        value = NumericalValue.of(0);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("0");
    }

    @Test
    public void writeHasDecimalAndIntegerHasZeroValue() throws IOException {
        value = NumericalValue.of(0).decimalValue(4);
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo(".4");
    }

    @Test
    public void writeWithDecimalAndUnitAndIntegerHasZeroValue() throws IOException {
        value = NumericalValue.of(0).decimalValue(4).unit("rem");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo(".4rem");
    }
}
