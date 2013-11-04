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

import com.salesforce.omakase.util.Util;
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
    public void toStringTest() {
        StringValue value = StringValue.of(QuotationMode.SINGLE, "xyz");
        assertThat(value.toString()).isNotEqualTo(Util.originalToString(value));
    }
}
