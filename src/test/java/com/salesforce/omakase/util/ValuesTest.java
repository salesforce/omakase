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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.declaration.*;
import com.salesforce.omakase.data.Keyword;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void filter() {
        NumericalValue t1 = NumericalValue.of(1, "px");
        KeywordValue t2 = KeywordValue.of("solid");
        KeywordValue t3 = KeywordValue.of("red");

        PropertyValue pv = PropertyValue.ofTerms(OperatorType.SPACE, t1, t2, t3);

        Iterable<KeywordValue> filtered = Values.filter(KeywordValue.class, pv);
        assertThat(filtered).containsExactly(t2, t3);
    }

    @Test
    public void filterWhenNoneMatching() {
        NumericalValue t1 = NumericalValue.of(1, "px");
        KeywordValue t2 = KeywordValue.of("solid");
        KeywordValue t3 = KeywordValue.of("red");

        PropertyValue pv = PropertyValue.ofTerms(OperatorType.SPACE, t1, t2, t3);

        Iterable<FunctionValue> filtered = Values.filter(FunctionValue.class, pv);
        assertThat(filtered).isEmpty();
    }

    @Test
    public void splitNotPresent() {
        PropertyValue pv = new PropertyValue();
        pv.append(NumericalValue.of(1));
        List<PropertyValue> split = Values.split(OperatorType.SLASH, pv);
        assertThat(split).hasSize(1);
        assertThat(split.get(0)).isNotSameAs(pv);
    }

    @Test
    public void splitOnePresent() {
        PropertyValue pv = new PropertyValue();
        pv.append(NumericalValue.of(1));
        pv.append(NumericalValue.of(1));
        pv.append(OperatorType.SLASH);
        pv.append(NumericalValue.of(2));
        pv.append(NumericalValue.of(2));
        pv.append(NumericalValue.of(2));

        List<PropertyValue> split = Values.split(OperatorType.SLASH, pv);
        assertThat(split).hasSize(2);
        assertThat(split.get(0).members()).hasSize(2);
        assertThat(split.get(1).members()).hasSize(3);
    }

    @Test
    public void splitTwoPresent() {
        PropertyValue pv = new PropertyValue();
        pv.append(NumericalValue.of(1));
        pv.append(OperatorType.SPACE);
        pv.append(NumericalValue.of(2));
        pv.append(OperatorType.SPACE);
        pv.append(NumericalValue.of(3));
        pv.append(NumericalValue.of(3));
        pv.append(OperatorType.COMMA);
        pv.append(NumericalValue.of(4));
        pv.append(OperatorType.COMMA);
        pv.append(NumericalValue.of(5));
        pv.append(NumericalValue.of(5));

        List<PropertyValue> split = Values.split(OperatorType.COMMA, pv);
        assertThat(split).hasSize(3);
        assertThat(split.get(0).members()).hasSize(6);
        assertThat(split.get(1).members()).hasSize(1);
        assertThat(split.get(2).members()).hasSize(2);
    }

    @Test
    public void joinOne() {
        PropertyValue pv = PropertyValue.of(new NumericalValue(1));
        PropertyValue join = Values.join(OperatorType.SLASH, Lists.newArrayList(pv));
        assertThat(join).isNotSameAs(pv);
        assertThat(join.members()).hasSize(1);
        assertThat(Iterables.get(join.members(), 0)).isInstanceOf(NumericalValue.class);
    }

    @Test
    public void joinSeveral() {
        PropertyValue pv1 = PropertyValue.of(new NumericalValue(1));
        PropertyValue pv2 = PropertyValue.of(new NumericalValue(2));
        PropertyValue pv3 = PropertyValue.ofTerms(OperatorType.COMMA, new NumericalValue(3), new NumericalValue(3));

        PropertyValue join = Values.join(OperatorType.SLASH, Lists.newArrayList(pv1, pv2, pv3));
        assertThat(join.members()).hasSize(7);
        assertThat(Iterables.get(join.members(), 0)).isInstanceOf(NumericalValue.class);
        assertThat(Iterables.get(join.members(), 1)).isInstanceOf(Operator.class);
        assertThat(Iterables.get(join.members(), 2)).isInstanceOf(NumericalValue.class);
        assertThat(Iterables.get(join.members(), 3)).isInstanceOf(Operator.class);
        assertThat(Iterables.get(join.members(), 4)).isInstanceOf(NumericalValue.class);
        assertThat(Iterables.get(join.members(), 5)).isInstanceOf(Operator.class);
        assertThat(Iterables.get(join.members(), 6)).isInstanceOf(NumericalValue.class);
    }

    @Test
    public void textualValueKeyword() {
        PropertyValue pv = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        assertThat(Values.textual(pv).get()).isEqualTo("none");
    }

    @Test
    public void textualValueString() {
        PropertyValue pv = PropertyValue.of(new StringValue(QuotationMode.SINGLE, "Times New Roman"));
        assertThat(Values.textual(pv).get()).isEqualTo("Times New Roman");
    }

    @Test
    public void textualValueMultipleTerms() {
        PropertyValue pv = PropertyValue.ofTerms(OperatorType.SPACE, NumericalValue.of(1), NumericalValue.of(1));
        assertThat(Values.textual(pv).isPresent()).isFalse();
    }
}
