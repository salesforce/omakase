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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link StyleWriter}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StyleWriterTest {
    @Test
    public void defaultMode() {
        assertThat(new StyleWriter().isInline()).isTrue();
    }

    @Test
    public void setMode() {
        StyleWriter writer = new StyleWriter();
        writer.mode(WriterMode.COMPRESSED);
        assertThat(writer.isCompressed()).isTrue();
    }

    @Test
    public void isVerbose() {
        assertThat(StyleWriter.verbose().isVerbose()).isTrue();
    }

    @Test
    public void isInline() {
        assertThat(StyleWriter.inline().isInline()).isTrue();
    }

    @Test
    public void isCompressed() {
        assertThat(StyleWriter.compressed().isCompressed()).isTrue();
    }

    @Test
    public void write() {
        StyleWriter writer = StyleWriter.compressed();
        Omakase.source(".test{color:red}").request(writer).process();
        assertThat(writer.write()).isEqualTo(".test{color:red}");
    }

    @Test
    public void writeToAppendable() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        StringBuilder builder = new StringBuilder();
        Omakase.source(".test{color:red}").request(writer).process();
        writer.writeTo(builder);
        assertThat(builder.toString()).isEqualTo(".test{color:red}");
    }

    @Test
    public void writeUnitHasOverride() {
        StyleWriter writer = StyleWriter.compressed();
        SampleCustomWriter sample = new SampleCustomWriter();
        writer.override(Selector.class, sample);
        Omakase.source(".test{color:red}").request(writer).process();
        writer.write();
        assertThat(sample.called).isTrue();
    }

    @Test
    public void writeUnitAsSnippetHasOverride() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        SampleCustomWriter sample = new SampleCustomWriter();
        writer.override(Selector.class, sample);
        writer.writeSnippet(new Selector(new ClassSelector("test")));
        assertThat(sample.called).isTrue();
    }

    public static final class SampleCustomWriter implements CustomWriter<Selector> {
        boolean called;

        @Override
        public void write(Selector unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            called = true;
        }
    }
}
