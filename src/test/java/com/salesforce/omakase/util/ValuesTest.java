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
}
