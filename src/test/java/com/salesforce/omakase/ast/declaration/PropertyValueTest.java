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
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Keyword;
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
        assertThat(val.declaration()).isNull();
    }

    @Test
    public void setParentDeclaration() {
        PropertyValue val = PropertyValue.of(NumericalValue.of(0));
        Declaration d = new Declaration(Property.FONT_SIZE, NumericalValue.of(1, "px"));
        val.declaration(d);
        assertThat(val.declaration()).isSameAs(d);
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
        public NonWritableTerm copy() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String textualValue() {
            throw new UnsupportedOperationException();
        }
    }
}
