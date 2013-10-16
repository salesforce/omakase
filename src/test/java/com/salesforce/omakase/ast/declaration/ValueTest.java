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

import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Value}. */
@SuppressWarnings("JavaDoc")
public class ValueTest {
    PropertyValue value;

    @Test
    public void asTermListPresent() {
        value = TermList.singleValue(NumericalValue.of(1));
        assertThat(Value.asTermList(value).isPresent()).isTrue();
    }

    @Test
    public void asTermListAbsent() {
        assertThat(Value.asTermList(new OtherPropertyValue()).isPresent()).isFalse();
    }

    @Test
    public void asHexColorPresentInTermList() {
        value = TermList.singleValue(new HexColorValue("fff"));
        assertThat(Value.asHexColor(value).isPresent()).isTrue();
    }

    @Test
    public void asHexColorNotPresentInTermList() {
        value = TermList.singleValue(new KeywordValue("none"));
        assertThat(Value.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asHexColorNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, HexColorValue.of("fff"), NumericalValue.of(1));
        assertThat(Value.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Value.asKeyword(value).isPresent()).isTrue();
    }

    @Test
    public void asKeywordNotPresentInTermList() {
        value = TermList.singleValue(new HexColorValue("fff"));
        assertThat(Value.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Value.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalPresentInTermList() {
        value = TermList.singleValue(NumericalValue.of(1, "px"));
        assertThat(Value.asNumerical(value).isPresent()).isTrue();
    }

    @Test
    public void asNumericalNotPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Value.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SLASH, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Value.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asStringPresentInTermList() {
        value = TermList.singleValue(StringValue.of(QuotationMode.DOUBLE, "helloworld"));
        assertThat(Value.asString(value).isPresent()).isTrue();
    }

    @Test
    public void asStringNotPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Value.asString(value).isPresent()).isFalse();
    }

    @Test
    public void asStringNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, StringValue.of(QuotationMode.DOUBLE, "h"), NumericalValue.of(1));
        assertThat(Value.asString(value).isPresent()).isFalse();
    }

    private static final class OtherPropertyValue extends AbstractPropertyValue implements PropertyValue {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public boolean isImportant() {
            return false;
        }

        @Override
        public PropertyValue important(boolean important) {
            return this;
        }
    }
}
