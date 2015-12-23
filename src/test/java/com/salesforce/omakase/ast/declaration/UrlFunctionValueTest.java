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

/**
 * Unit tests for {@link UrlFunctionValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UrlFunctionValueTest {
    @Test
    public void getUrl() {
        UrlFunctionValue url = new UrlFunctionValue(2, 2, "/images/one.png");
        assertThat(url.url()).isEqualTo("/images/one.png");
    }

    @Test
    public void setUrl() {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.url("/images/two.png?cb=1");
        assertThat(url.url()).isEqualTo("/images/two.png?cb=1");
    }

    @Test
    public void defaultNoQuotationMode() {
        assertThat(new UrlFunctionValue("test").quotationMode().isPresent()).isFalse();
    }

    @Test
    public void setQuotationMode() {
        UrlFunctionValue url = new UrlFunctionValue("test");
        url.quotationMode(QuotationMode.DOUBLE);
        assertThat(url.quotationMode().isPresent()).isTrue();
        assertThat(url.quotationMode().get()).isEqualTo(QuotationMode.DOUBLE);
    }

    @Test
    public void textualValueReturnsUrl() {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.quotationMode(QuotationMode.DOUBLE);
        assertThat(url.url()).isEqualTo("/images/one.png");
    }

    @Test
    public void writeSingleQuotes() throws IOException {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.quotationMode(QuotationMode.SINGLE);
        assertThat(StyleWriter.verbose().writeSnippet(url)).isEqualTo("url('/images/one.png')");
    }

    @Test
    public void writeDoubleQuotes() throws IOException {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.quotationMode(QuotationMode.DOUBLE);
        assertThat(StyleWriter.verbose().writeSnippet(url)).isEqualTo("url(\"/images/one.png\")");
    }

    @Test
    public void writeNoQuotes() throws IOException {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        assertThat(StyleWriter.verbose().writeSnippet(url)).isEqualTo("url(/images/one.png)");
    }

    @Test
    public void testCopy() {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.quotationMode(QuotationMode.DOUBLE);
        url.comments(Lists.newArrayList("test"));

        UrlFunctionValue copy = (UrlFunctionValue)url.copy();
        assertThat(copy.url()).isSameAs(url.url());
        assertThat(copy.quotationMode().get()).isSameAs(url.quotationMode().get());
        assertThat(copy.comments()).hasSameSizeAs(url.comments());
    }

    @Test
    public void testCopyNoQuotes() {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        url.comments(Lists.newArrayList("test"));

        UrlFunctionValue copy = (UrlFunctionValue)url.copy();
        assertThat(copy.url()).isSameAs(url.url());
        assertThat(copy.quotationMode().isPresent()).isEqualTo(false);
        assertThat(copy.comments()).hasSameSizeAs(url.comments());
    }
}
