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

/** Unit tests for {@link PseudoElementSelector}. */
@SuppressWarnings("JavaDoc")
public class PseudoElementSelectorTest {
    @Test
    public void getName() {
        PseudoElementSelector s = new PseudoElementSelector(5, 5, "before");
        assertThat(s.name()).isEqualTo("before");
    }

    @Test
    public void setName() {
        PseudoElementSelector s = new PseudoElementSelector(5, 5, "before");
        s.name("after");
        assertThat(s.name()).isEqualTo("after");
    }

    @Test
    public void type() {
        assertThat(new PseudoElementSelector(5, 5, "before").type()).isSameAs(SelectorPartType.PSEUDO_ELEMENT_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        PseudoElementSelector s = new PseudoElementSelector("selection");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo("::selection");
    }

    @Test
    public void writeBefore() throws IOException {
        PseudoElementSelector s = new PseudoElementSelector("before");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo(":before");
    }

    @Test
    public void writeAfter() throws IOException {
        PseudoElementSelector s = new PseudoElementSelector("after");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo(":after");
    }

    @Test
    public void writeFirstLine() throws IOException {
        PseudoElementSelector s = new PseudoElementSelector("first-line");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo(":first-line");
    }

    @Test
    public void writeFirstLetter() throws IOException {
        PseudoElementSelector s = new PseudoElementSelector("first-letter");
        assertThat(StyleWriter.compressed().writeSnippet(s)).isEqualTo(":first-letter");
    }

    @Test
    public void toStringTest() {
        PseudoElementSelector s = new PseudoElementSelector("first-letter");
        assertThat(s.toString()).isNotEqualTo(Util.originalToString(s));
    }
}
