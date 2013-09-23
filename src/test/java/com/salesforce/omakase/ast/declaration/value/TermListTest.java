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

import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.test.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link TermList}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class TermListTest {
    @Test
    public void position() {
        TermList tl = new TermList(5, 2);
        assertThat(tl.line()).isEqualTo(5);
        assertThat(tl.column()).isEqualTo(2);
    }

    @Test
    public void membersWhenEmpty() {
        assertThat(new TermList(5, 5).members()).isEmpty();
    }

    @Test
    public void addMember() {
        NumericalValue number = NumericalValue.of(1);
        HexColorValue hex = HexColorValue.of("#333");
        TermOperator operator = TermOperator.SPACE;

        TermList tl = TermList.singleValue(number);
        tl.add(operator).add(hex);

        assertThat(tl.members()).containsExactly(number, operator, hex);
    }

    @Test
    public void terms() {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        TermList tl = TermList.ofValues(TermOperator.SPACE, n1, n2);

        assertThat(tl.members()).hasSize(3);
        assertThat(tl.terms()).containsExactly(n1, n2);
    }

    @Test
    public void defaultNotImportant() {
        assertThat(TermList.singleValue(KeywordValue.of("test")).isImportant()).isFalse();
    }

    @Test
    public void setImportant() {
        TermList tl = TermList.singleValue(NumericalValue.of(1));
        tl.important(true);
        assertThat(tl.isImportant()).isTrue();
        tl.important(false);
        assertThat(tl.isImportant()).isFalse();
    }

    @Test
    public void propogatesBroadcastToMembers() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        NumericalValue number = NumericalValue.of(1);
        KeywordValue keyword = KeywordValue.of("noen");
        TermList tl = TermList.ofValues(TermOperator.SPACE, number, keyword);

        tl.propagateBroadcast(qb);

        assertThat(qb.all()).containsExactly(tl, number, keyword);
    }

    @Test
    public void writeWhenNotImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        TermList tl = TermList.ofValues(TermOperator.SPACE, n1, n2);

        assertThat(StyleWriter.compressed().writeSnippet(tl)).isEqualTo("1 2");
    }

    @Test
    public void writeVerboseWhenImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        TermList tl = TermList.ofValues(TermOperator.SPACE, n1, n2);
        tl.important(true);

        assertThat(StyleWriter.verbose().writeSnippet(tl)).isEqualTo("1 2 !important");
    }

    @Test
    public void writeCompressedWhenImportant() throws IOException {
        NumericalValue n1 = NumericalValue.of(1);
        NumericalValue n2 = NumericalValue.of(2);
        TermList tl = TermList.ofValues(TermOperator.SPACE, n1, n2);
        tl.important(true);

        assertThat(StyleWriter.compressed().writeSnippet(tl)).isEqualTo("1 2!important");
    }

    @Test
    public void toStringTest() {
        TermList tl = TermList.singleValue(NumericalValue.of(1));
        assertThat(tl.toString()).isNotEqualTo(Util.originalToString(tl));
    }
}
