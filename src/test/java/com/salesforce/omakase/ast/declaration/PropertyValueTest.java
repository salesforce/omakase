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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Browser;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.test.functional.StatusChangingBroadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link PropertyValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class PropertyValueTest {
    @Test
    public void position() {
        PropertyValue val = new PropertyValue(5, 2, new StatusChangingBroadcaster());
        assertThat(val.line()).isEqualTo(5);
        assertThat(val.column()).isEqualTo(2);
    }

    @Test
    public void membersWhenEmpty() {
        assertThat(new PropertyValue().members()).isEmpty();
    }

    @Test
    public void addMember() {
        NumericalValue number = NumericalValue.of(1);
        HexColorValue hex = HexColorValue.of("#333");
        Operator operator = new Operator(OperatorType.SPACE);

        PropertyValue value = PropertyValue.of(number);
        value.append(operator).append(hex);

        assertThat(value.members()).containsExactly(number, operator, hex);
    }

    @Test
    public void terms() {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, n1, n2);

        assertThat(val.members()).hasSize(3);
        assertThat(val.terms()).containsExactly(n1, n2);
    }

    @Test
    public void defaultNotImportant() {
        assertThat(PropertyValue.of(KeywordValue.of("test")).isImportant()).isFalse();
    }

    @Test
    public void setImportant() {
        PropertyValue val = PropertyValue.of(NumericalValue.of(1));
        val.important(true);
        assertThat(val.isImportant()).isTrue();
        val.important(false);
        assertThat(val.isImportant()).isFalse();
    }

    @Test
    public void propogatesBroadcastToMembers() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        NumericalValue number = NumericalValue.of(1);
        KeywordValue keyword = KeywordValue.of("noen");

        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, number, keyword);

        val.propagateBroadcast(qb);

        assertThat(qb.all()).containsExactly(number, keyword, val);
    }

    @Test
    public void isWritableIfHasNonDetachedTerm() {
        PropertyValue val = PropertyValue.of(NumericalValue.of(1));
        assertThat(val.isWritable()).isTrue();
    }

    @Test
    public void isNotWritableWhenNoTermsAreWritable() {
        PropertyValue val = PropertyValue.of(new NonWritableTerm());
        assertThat(val.isWritable()).isFalse();
    }

    @Test
    public void isNotWritableWhenNoTerms() {
        PropertyValue val = new PropertyValue();
        assertThat(val.isWritable()).isFalse();
    }

    @Test
    public void writeWhenNotImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, n1, n2);

        assertThat(StyleWriter.compressed().writeSnippet(val)).isEqualTo("1 2");
    }

    @Test
    public void writeVerboseWhenImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, n1, n2);
        val.important(true);

        assertThat(StyleWriter.verbose().writeSnippet(val)).isEqualTo("1 2 !important");
    }

    @Test
    public void writeCompressedWhenImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, n1, n2);
        val.important(true);

        assertThat(StyleWriter.compressed().writeSnippet(val)).isEqualTo("1 2!important");
    }

    @Test
    public void defaultNoParentDeclaration() {
        PropertyValue val = PropertyValue.of(NumericalValue.of(1));
        assertThat(val.declaration().isPresent()).isFalse();
    }

    @Test
    public void setParentDeclaration() {
        PropertyValue val = PropertyValue.of(NumericalValue.of(0));
        Declaration d = new Declaration(Property.FONT_SIZE, NumericalValue.of(1, "px"));
        val.declaration(d);
        assertThat(val.declaration().get()).isSameAs(d);
    }

    @Test
    public void testCopy() {
        PropertyValue val = PropertyValue.ofTerms(OperatorType.SPACE, NumericalValue.of(0), NumericalValue.of(0));
        val.important(true);
        val.comments(Lists.newArrayList("test"));

        PropertyValue copy = val.copy();
        assertThat(copy.isImportant()).isTrue();
        assertThat(copy.members()).hasSize(3);
        assertThat(copy.comments()).hasSize(1);
    }

    @Test
    public void testCopyWithPrefix() {
        PropertyValue val = PropertyValue.of(new GenericFunctionValue("calc", "2px-1px"));
        val.important(true);
        val.comments(Lists.newArrayList("test"));

        SupportMatrix support = new SupportMatrix();
        support.browser(Browser.FIREFOX, 15);

        PropertyValue copy = val.copy(Prefix.MOZ, support);
        assertThat(copy.isImportant()).isTrue();
        assertThat(copy.members()).hasSize(1);
        assertThat(copy.comments()).hasSize(1);
        PropertyValueMember first = Iterables.get(copy.members(), 0);
        assertThat(((GenericFunctionValue)first).name()).isEqualTo("-moz-calc");
    }

    @Test
    public void textualValueKeyword() {
        PropertyValue pv = PropertyValue.of(KeywordValue.of(Keyword.NONE));
        assertThat(pv.singleTextualValue().get()).isEqualTo("none");
    }

    @Test
    public void textualValueString() {
        PropertyValue pv = PropertyValue.of(new StringValue(QuotationMode.SINGLE, "Times New Roman"));
        assertThat(pv.singleTextualValue().get()).isEqualTo("Times New Roman");
    }

    @Test
    public void textualValueMultipleTerms() {
        PropertyValue pv = PropertyValue.ofTerms(OperatorType.SPACE, NumericalValue.of(1), NumericalValue.of(1));
        assertThat(pv.singleTextualValue().isPresent()).isFalse();
    }

    private static final class NonWritableTerm extends AbstractTerm {
        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        protected PropertyValueMember makeCopy(Prefix prefix, SupportMatrix support) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String textualValue() {
            throw new UnsupportedOperationException();
        }
    }
}
