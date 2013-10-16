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

import com.salesforce.omakase.test.util.Util;
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
    public void toStringTest() {
        UrlFunctionValue url = new UrlFunctionValue("/images/one.png");
        assertThat(url.toString()).isNotEqualTo(Util.originalToString(url));
    }
}
