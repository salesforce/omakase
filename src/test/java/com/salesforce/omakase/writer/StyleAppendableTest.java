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

package com.salesforce.omakase.writer;

import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StyleAppendable}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StyleAppendableTest {
    @Test
    public void appendChar() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append('a');
        assertThat(sa.toString()).isEqualTo("a");
    }

    @Test
    public void appendString() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append("abc");
        assertThat(sa.toString()).isEqualTo("abc");
    }

    @Test
    public void appendInt() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append(1);
        assertThat(sa.toString()).isEqualTo("1");
    }

    @Test
    public void appendDouble() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append(1d);
        assertThat(sa.toString()).isEqualTo("1.0");
    }

    @Test
    public void appendLong() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.append(1l);
        assertThat(sa.toString()).isEqualTo("1");
    }

    @Test
    public void newline() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newline();
        assertThat(sa.toString()).isEqualTo("\n");
    }

    @Test
    public void newlineIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newlineIf(true);
        assertThat(sa.toString()).isEqualTo("\n");
    }

    @Test
    public void newlineIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.newlineIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void space() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.space();
        assertThat(sa.toString()).isEqualTo(" ");
    }

    @Test
    public void spaceIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.spaceIf(true);
        assertThat(sa.toString()).isEqualTo(" ");
    }

    @Test
    public void spaceIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.spaceIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void defaultNoIndentation() {
        StyleAppendable sa = new StyleAppendable();
        assertThat(sa.indentationLevel()).isEqualTo(0);
    }

    @Test
    public void cantUnindentBelowZero() {
        StyleAppendable sa = new StyleAppendable();
        sa.unindent();
        assertThat(sa.indentationLevel()).isEqualTo(0);
    }

    @Test
    public void indent() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indent();
        assertThat(sa.indentationLevel()).isEqualTo(1);

        sa.indent();
        assertThat(sa.indentationLevel()).isEqualTo(2);

        sa.unindent();
        assertThat(sa.indentationLevel()).isEqualTo(1);
    }

    @Test
    public void indentIfTrue() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(true);
        assertThat(sa.indentationLevel()).isEqualTo(1);
    }

    @Test
    public void indentIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(false);
        assertThat(sa.indentationLevel()).isEqualTo(0);
    }

    @Test
    public void unindentIfTrue() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indent();
        sa.unindentIf(true);
        assertThat(sa.indentationLevel()).isEqualTo(0);
    }

    @Test
    public void unindentIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indent();
        sa.unindentIf(false);
        assertThat(sa.indentationLevel()).isEqualTo(1);
    }

    @Test
    public void appendToGiven() throws IOException {
        StringBuilder b = new StringBuilder();
        StyleAppendable sa = new StyleAppendable(b);
        sa.append('c');
        assertThat(b.toString()).isEqualTo("c");
    }
}
