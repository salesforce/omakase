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

import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link KeywordValue}. */
@SuppressWarnings("JavaDoc")
public class KeywordValueTest {
    private KeywordValue value;

    @Test
    public void positioning() {
        value = new KeywordValue(5, 2, "red");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsKeyword() {
        value = new KeywordValue("red");
        assertThat(value.keyword()).isEqualTo("red");
    }

    @Test
    public void setsKeywordFromKeyword() {
        value = new KeywordValue("red");
        value.keyword(Keyword.NONE);
        assertThat(value.keyword()).isEqualTo("none");
    }

    @Test
    public void setsKeywordFromString() {
        value = new KeywordValue("red");
        value.keyword("none");
        assertThat(value.keyword()).isEqualTo("none");
    }

    @Test
    public void textualValueReturnsKeyword() {
        value = new KeywordValue("red");
        assertThat(value.textualValue()).isEqualTo("red");
    }

    @Test
    public void writeVerbose() throws IOException {
        value = KeywordValue.of("absolute");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("absolute");
    }

    @Test
    public void writeInline() throws IOException {
        value = KeywordValue.of(Keyword.ABSOLUTE);
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(value)).isEqualTo("absolute");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = KeywordValue.of(Keyword.INLINE_BLOCK);
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(value)).isEqualTo("inline-block");
    }

    @Test
    public void copy() {
        value = KeywordValue.of(Keyword.INLINE_BLOCK);
        assertThat(((KeywordValue)value.copy()).asKeyword().get()).isSameAs(Keyword.INLINE_BLOCK);
    }
}
