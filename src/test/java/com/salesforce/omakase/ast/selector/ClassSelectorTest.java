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

package com.salesforce.omakase.ast.selector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.salesforce.omakase.writer.StyleWriter;

/** Unit tests for {@link ClassSelector]. */
public class ClassSelectorTest {
    @Test
    public void getName() {
        ClassSelector cs = new ClassSelector(1, 1, "test");
        assertThat(cs.name()).isEqualTo("test");
    }

    @Test
    public void setName() {
        ClassSelector cs = new ClassSelector(1, 1, "test");
        cs.name("test2");
        assertThat(cs.name()).isEqualTo("test2");
    }

    @Test
    public void type() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.type()).isSameAs(SelectorPartType.CLASS_SELECTOR);
    }

    @Test
    public void write() throws IOException {
        ClassSelector cs = new ClassSelector("test");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSingle(cs)).isEqualTo(".test");
    }

    @Test
    public void copy() {
        ClassSelector cs = new ClassSelector("test");
        assertThat(cs.copy().name()).isEqualTo("test");
    }
}
