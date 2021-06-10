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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.writer.StyleWriter;

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
    public void dynamicConstructionLowerCases() {
        value = new HexColorValue("FFF", false);
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
    public void textualValueIncludesNumberOnly() {
        value = new HexColorValue(5, 2, "123123");
        assertThat(value.textualValue()).isEqualTo("123123");
    }

    @Test
    public void writeVerbose() throws IOException {
        value = HexColorValue.of("fff");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSingle(value)).isEqualTo("#fff");
    }

    @Test
    public void writeInline() throws IOException {
        value = HexColorValue.of("#fff");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSingle(value)).isEqualTo("#fff");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = HexColorValue.of("a1f3f2");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSingle(value)).isEqualTo("#a1f3f2");
    }

    @Test
    public void copyTest() {
        value = HexColorValue.of("#123123");
        value.comments(Lists.newArrayList("test"));

        HexColorValue copy = value.copy();
        assertThat(copy.color()).isEqualTo(value.color());
        assertThat(copy.comments()).hasSameSizeAs(value.comments());
    }
}
