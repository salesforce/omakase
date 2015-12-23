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
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link StringValue}. */
@SuppressWarnings("JavaDoc")
public class StringValueTest {
    @Test
    public void positioning() {
        StringValue s = new StringValue(3, 1, QuotationMode.SINGLE, "test");
        assertThat(s.line()).isEqualTo(3);
        assertThat(s.column()).isEqualTo(1);
    }

    @Test
    public void getContent() {
        StringValue s = new StringValue(QuotationMode.DOUBLE, "test");
        assertThat(s.content()).isEqualTo("test");
    }

    @Test
    public void setContent() {
        StringValue s = new StringValue(QuotationMode.DOUBLE, "test");
        s.content(QuotationMode.SINGLE, "test2");
        assertThat(s.content()).isEqualTo("test2");
    }

    @Test
    public void getMode() {
        StringValue s = new StringValue(QuotationMode.DOUBLE, "test");
        assertThat(s.mode()).isSameAs(QuotationMode.DOUBLE);
    }

    @Test
    public void textualValueReturnsContent() {
        StringValue s = new StringValue(QuotationMode.DOUBLE, "test");
        assertThat(s.textualValue()).isEqualTo("test");
    }

    @Test
    public void writeVerbose() throws IOException {
        StringValue s = StringValue.of(QuotationMode.SINGLE, "xyz");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(s)).isEqualTo("'xyz'");

        s.content(QuotationMode.DOUBLE, "xyz");
        assertThat(writer.writeSnippet(s)).isEqualTo("\"xyz\"");
    }

    @Test
    public void writeInline() throws IOException {
        StringValue s = StringValue.of(QuotationMode.SINGLE, "xyz");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(s)).isEqualTo("'xyz'");
    }

    @Test
    public void writeCompressed() throws IOException {
        StringValue s = StringValue.of(QuotationMode.SINGLE, "xyz");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(s)).isEqualTo("'xyz'");
    }

    @Test
    public void copyTest() {
        StringValue s = StringValue.of(QuotationMode.SINGLE, "xyz");
        s.comments(Lists.newArrayList("test"));

        StringValue copy = (StringValue)s.copy();
        assertThat(copy.content()).isEqualTo(s.content());
        assertThat(copy.mode()).isSameAs(s.mode());
        assertThat(copy.comments()).hasSameSizeAs(s.comments());
    }
}
