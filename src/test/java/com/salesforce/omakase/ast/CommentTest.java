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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.util.Util;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Comment}. */
@SuppressWarnings("JavaDoc")
public class CommentTest {
    @Test
    public void content() {
        Comment c = new Comment("test");
        assertThat(c.content()).isEqualTo("test");
    }

    @Test
    public void isWritable() {
        assertThat(new Comment("test").isWritable()).isTrue();
    }

    @Test
    public void writeVerbose() throws IOException {
        Comment c = new Comment(" test ");
        StyleWriter writer = StyleWriter.verbose();
        assertThat(writer.writeSnippet(c)).isEqualTo("/* test */");
    }

    @Test
    public void writeInline() throws IOException {
        Comment c = new Comment(" test ");
        StyleWriter writer = StyleWriter.inline();
        assertThat(writer.writeSnippet(c)).isEqualTo("");
    }

    @Test
    public void writeCompressed() throws IOException {
        Comment c = new Comment(" test ");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(c)).isEqualTo("");
    }

    @Test
    public void toStringTest() {
        Comment c = new Comment(" test ");
        assertThat(c.toString()).isNotEqualTo(Util.originalToString(c));
    }
}
