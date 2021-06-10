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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;
import com.salesforce.omakase.writer.StyleWriter;

/** Unit tests for {@link GenericFunctionValue}. */
@SuppressWarnings("JavaDoc")
public class GenericFunctionValueTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    GenericFunctionValue value;

    @Test
    public void positioning() {
        value = new GenericFunctionValue(5, 2, "url", "home.png");
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void getsName() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.name()).isEqualTo("url");
    }

    @Test
    public void setsName() {
        value = new GenericFunctionValue("rgb", "255,255,255");
        value.name("rgba");
        assertThat(value.name()).isEqualTo("rgba");
    }

    @Test
    public void getsArgs() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.args()).isEqualTo("home.png");
    }

    @Test
    public void setsArgs() {
        value = new GenericFunctionValue("rgb", "255,255,255");
        value.args("255, 255, 255, 0.5");
        assertThat(value.args()).isEqualTo("255, 255, 255, 0.5");
    }

    @Test
    public void textualValueReturnsArgs() {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(value.textualValue()).isEqualTo("home.png");
    }

    @Test
    public void writeVerbose() throws IOException {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(StyleWriter.verbose().writeSingle(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeInline() throws IOException {
        value = new GenericFunctionValue("url", "home.png");
        assertThat(StyleWriter.inline().writeSingle(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeCompressed() throws IOException {
        value = GenericFunctionValue.of("url", "home.png");
        assertThat(StyleWriter.compressed().writeSingle(value)).isEqualTo("url(home.png)");
    }

    @Test
    public void writeCompressedWhitespace() throws IOException {
        value = GenericFunctionValue.of("url", "  hello   world \n\n NYC ");
        assertThat(StyleWriter.compressed().writeSingle(value)).isEqualTo("url(hello world NYC)");
    }

    @Test
    public void writeEmptyArgs() throws IOException {
        value = new GenericFunctionValue("xyz", "");
        assertThat(StyleWriter.verbose().writeSingle(value)).isEqualTo("xyz()");
    }

    @Test
    public void copy() {
        value = new GenericFunctionValue("test", "args");
        value.comments(Lists.newArrayList("test"));

        GenericFunctionValue copy = value.copy();
        assertThat(copy.name()).isEqualTo("test");
        assertThat(copy.args()).isEqualTo("args");
        assertThat(copy.comments()).hasSameSizeAs(value.comments());
    }
}
