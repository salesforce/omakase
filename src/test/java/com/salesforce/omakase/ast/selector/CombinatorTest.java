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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Combinator}. */
@SuppressWarnings("JavaDoc")
public class CombinatorTest {
    @Test
    public void typeWhenDescendant() {
        Combinator c = Combinator.descendant();
        assertThat(c.type()).isSameAs(SelectorPartType.DESCENDANT_COMBINATOR);
    }

    @Test
    public void typeWhenChild() {
        Combinator c = Combinator.child();
        assertThat(c.type()).isSameAs(SelectorPartType.CHILD_COMBINATOR);
    }

    @Test
    public void typeWhenAdjacent() {
        Combinator c = Combinator.adjacent();
        assertThat(c.type()).isSameAs(SelectorPartType.ADJACENT_SIBLING_COMBINATOR);
    }

    @Test
    public void typeWhenGeneral() {
        Combinator c = Combinator.general();
        assertThat(c.type()).isSameAs(SelectorPartType.GENERAL_SIBLING_COMBINATOR);
    }

    @Test
    public void writeDescendant() throws IOException {
        Combinator c = Combinator.descendant();
        assertThat(StyleWriter.compressed().writeSnippet(c)).isEqualTo(" ");
    }

    @Test
    public void writeChild() throws IOException {
        Combinator c = Combinator.child();
        assertThat(StyleWriter.compressed().writeSnippet(c)).isEqualTo(">");
    }

    @Test
    public void writeAdjacent() throws IOException {
        Combinator c = Combinator.adjacent();
        assertThat(StyleWriter.compressed().writeSnippet(c)).isEqualTo("+");
    }

    @Test
    public void writeGeneral() throws IOException {
        Combinator c = Combinator.general();
        assertThat(StyleWriter.compressed().writeSnippet(c)).isEqualTo("~");
    }

    @Test
    public void toStringTest() {
        Combinator c = Combinator.general();
        assertThat(c.toString()).isNotEqualTo(Util.originalToString(c));
    }
}
