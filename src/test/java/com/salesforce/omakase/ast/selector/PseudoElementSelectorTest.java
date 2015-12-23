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

package com.salesforce.omakase.ast.selector;

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
    public void copy() {
        PseudoElementSelector s = new PseudoElementSelector("first-letter");
        assertThat(((PseudoElementSelector)s.copy()).name()).isEqualTo("first-letter");
    }
}
