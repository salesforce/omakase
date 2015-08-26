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
