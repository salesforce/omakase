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

import org.junit.Test;

import com.google.common.collect.Lists;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link LinearGradientFunctionValue}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class LinearGradientFunctionValueTest {
    private LinearGradientFunctionValue function;

    @Test
    public void getArgs() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.args()).isEqualTo("red, yellow");
    }

    @Test
    public void setArgs() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.args("yellow, red");
        assertThat(function.args()).isEqualTo("yellow, red");
    }

    @Test
    public void defaultNotRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.repeating()).isFalse();
    }

    @Test
    public void setRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(function.repeating()).isTrue();
    }

    @Test
    public void nameWhenNotRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.name()).isEqualTo("linear-gradient");
    }

    @Test
    public void nameWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(function.name()).isEqualTo("repeating-linear-gradient");
    }

    @Test
    public void testPrefix() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.prefix().isPresent()).isFalse();
        function.prefix(Prefix.MOZ);
        assertThat(function.prefix().get()).isSameAs(Prefix.MOZ);
    }

    @Test
    public void nameWhenPrefixedAndRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        function.prefix(Prefix.MOZ);
        assertThat(function.name()).isEqualTo("-moz-repeating-linear-gradient");
    }

    @Test
    public void nameWhenPrefixedAndNotRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.prefix(Prefix.MOZ);
        assertThat(function.name()).isEqualTo("-moz-linear-gradient");
    }

    @Test
    public void textualValueReturnsArgs() {
        function = new LinearGradientFunctionValue("red, yellow");
        assertThat(function.textualValue()).isEqualTo("red, yellow");
    }

    @Test
    public void testWrite() {
        function = new LinearGradientFunctionValue("to top, red, #fcc");
        assertThat(StyleWriter.inline().writeSingle(function)).isEqualTo("linear-gradient(to top, red, #fcc)");
    }

    @Test
    public void testWriteWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow").repeating(true);
        assertThat(StyleWriter.inline().writeSingle(function)).isEqualTo("repeating-linear-gradient(red, yellow)");
    }

    @Test
    public void testCopy() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.comments(Lists.newArrayList("test"));
        LinearGradientFunctionValue copy = function.copy();
        assertThat(copy.args()).isEqualTo(function.args());
        assertThat(copy.comments()).hasSameSizeAs(function.comments());
        assertThat(copy.repeating()).isEqualTo(function.repeating());
    }

    @Test
    public void testCopyWhenRepeating() {
        function = new LinearGradientFunctionValue("red, yellow");
        function.comments(Lists.newArrayList("test"));
        function.repeating(true);
        LinearGradientFunctionValue copy = function.copy();
        assertThat(copy.args()).isEqualTo(function.args());
        assertThat(copy.comments()).hasSameSizeAs(function.comments());
        assertThat(copy.repeating()).isEqualTo(function.repeating());
    }
}
