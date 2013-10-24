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
    public void indent() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indent();
        assertThat(sa.toString()).isEqualTo("  ");
    }

    @Test
    public void indentIf() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(true);
        assertThat(sa.toString()).isEqualTo("  ");
    }

    @Test
    public void indentIfFalse() throws IOException {
        StyleAppendable sa = new StyleAppendable();
        sa.indentIf(false);
        assertThat(sa.toString()).isEqualTo("");
    }

    @Test
    public void appendToGiven() throws IOException {
        StringBuilder b = new StringBuilder();
        StyleAppendable sa = new StyleAppendable(b);
        sa.append('c');
        assertThat(b.toString()).isEqualTo("c");
    }
}
