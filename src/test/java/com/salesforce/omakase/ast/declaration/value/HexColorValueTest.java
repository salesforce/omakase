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

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link HexColorValue}. */
@SuppressWarnings("JavaDoc")
public class HexColorValueTest {
    private HexColorValue value;

    @Test
    public void positioning() {
        value = new HexColorValue(5, 2, "333111");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsColor() {
        value = new HexColorValue(5, 2, "123123");
        assertThat(value.color()).isEqualTo("123123");
    }

    @Test
    public void setsColor() {
        value = new HexColorValue(5, 2, "333111");
        value.color("fff");
        assertThat(value.color()).isEqualTo("fff");
    }

    @Test
    public void colorLowerCased() {
        value = HexColorValue.of("FFF");
        assertThat(value.color()).isEqualTo("fff");
    }

    @Test
    public void removesSymbol() {
        value = HexColorValue.of("#fff");
        assertThat(value.color()).isEqualTo("fff");
        value.color("#aaa");
        assertThat(value.color()).isEqualTo("aaa");
    }

    @Test
    public void isShorthandTrue() {
        value = new HexColorValue("fff");
        assertThat(value.isShorthand()).isTrue();
    }

    @Test
    public void isShorthandFalse() {
        value = new HexColorValue("ffffff");
        assertThat(value.isShorthand()).isFalse();
    }

    @Test
    public void writeVerbose() throws IOException {
        value = HexColorValue.of("fff");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(value)).isEqualTo("#fff");
    }

    @Test
    public void writeInline() throws IOException {
        value = HexColorValue.of("#fff");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(value)).isEqualTo("#fff");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = HexColorValue.of("a1f3f2");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(value)).isEqualTo("#a1f3f2");
    }
}
