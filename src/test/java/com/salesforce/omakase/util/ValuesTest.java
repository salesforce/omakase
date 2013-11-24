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

package com.salesforce.omakase.util;

import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.StringValue;
import com.salesforce.omakase.data.Keyword;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Values}. */
@SuppressWarnings("JavaDoc")
public class ValuesTest {
    PropertyValue value;

    @Test
    public void asHexColorPresentInPropertyValue() {
        value = PropertyValue.of(new HexColorValue("fff"));
        assertThat(Values.asHexColor(value).isPresent()).isTrue();
    }

    @Test
    public void asHexColorNotPresentInPropertyValue() {
        value = PropertyValue.of(new KeywordValue("none"));
        assertThat(Values.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asHexColorNotOnlyOneInPropertyValue() {
        value = PropertyValue.ofTerms(OperatorType.SPACE, HexColorValue.of("fff"), NumericalValue.of(1));
        assertThat(Values.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordPresentInPropertyValue() {
        value = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asKeyword(value).isPresent()).isTrue();
    }

    @Test
    public void asKeywordNotPresentInPropertyValue() {
        value = PropertyValue.of(new HexColorValue("fff"));
        assertThat(Values.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordNotOnlyOneInPropertyValue() {
        value = PropertyValue.ofTerms(OperatorType.SPACE, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Values.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalPresentInPropertyValue() {
        value = PropertyValue.of(NumericalValue.of(1, "px"));
        assertThat(Values.asNumerical(value).isPresent()).isTrue();
    }

    @Test
    public void asNumericalNotPresentInPropertyValue() {
        value = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalNotOnlyOneInPropertyValue() {
        value = PropertyValue.ofTerms(OperatorType.SLASH, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Values.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asStringPresentInPropertyValue() {
        value = PropertyValue.of(StringValue.of(QuotationMode.DOUBLE, "helloworld"));
        assertThat(Values.asString(value).isPresent()).isTrue();
    }

    @Test
    public void asStringNotPresentInPropertyValue() {
        value = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asString(value).isPresent()).isFalse();
    }

    @Test
    public void asStringNotOnlyOneInPropertyValue() {
        value = PropertyValue.ofTerms(OperatorType.SPACE, StringValue.of(QuotationMode.DOUBLE, "h"), NumericalValue.of(1));
        assertThat(Values.asString(value).isPresent()).isFalse();
    }
}
