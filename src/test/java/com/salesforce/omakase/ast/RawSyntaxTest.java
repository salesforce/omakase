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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link RawSyntax}. */
@SuppressWarnings("JavaDoc")
public class RawSyntaxTest {
    @Test
    public void line() {
        assertThat(new RawSyntax(5, 10, ".class > #id").line()).isEqualTo(5);
    }

    @Test
    public void column() {
        assertThat(new RawSyntax(5, 10, ".class > #id").column()).isEqualTo(10);
    }

    @Test
    public void getContent() {
        RawSyntax raw = new RawSyntax(1, 1, "test");
        assertThat(raw.content()).isEqualTo("test");
    }

    @Test
    public void testCopy() {
        RawSyntax raw = new RawSyntax(1, 1, "test");
        assertThat(raw.copy().content()).isEqualTo(raw.content());
    }

    @Test
    public void writeVerbose() throws IOException {
        RawSyntax r = new RawSyntax(5, 5, ".class > #id");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(r)).isEqualTo(".class > #id");
    }
}
