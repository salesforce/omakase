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

/** Unit tests for {@link TermOperator}. */
@SuppressWarnings("JavaDoc")
public class TermOperatorTest {
    @Test
    public void alwaysWritable() {
        for (TermOperator o : TermOperator.values()) {
            assertThat(o.isWritable()).isTrue();
        }
    }

    @Test
    public void hasToken() {
        for (TermOperator o : TermOperator.values()) {
            assertThat(o.token()).isNotNull();
        }
    }

    @Test
    public void writeComa() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(TermOperator.COMMA)).isEqualTo(",");
    }

    @Test
    public void writeSlash() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(TermOperator.SLASH)).isEqualTo("/");
    }

    @Test
    public void writeSpace() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(TermOperator.SPACE)).isEqualTo(" ");
    }
}

