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
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
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
        assertThat(writer.mode()).isSameAs(WriterMode.COMPRESSED);
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
        Omakase.source(".test{color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo(".test{color:red}");
    }

    @Test
    public void writeWithComments() {
        StyleWriter writer = StyleWriter.compressed().writeComments(true);
        Omakase.source("/*test*/.test{color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*test*/.test{color:red}");
    }

    @Test
    public void writeWithCommentsOnlyAnnotated() {
        StyleWriter writer = StyleWriter.compressed().writeComments(true, true);
        Omakase.source("/*@yes*/.test{/*no*/color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*@yes*/.test{color:red}");
    }

    @Test
    public void writeWithOrphanedComments() {
        StyleWriter writer = StyleWriter.compressed().writeComments(true);
        Omakase.source(".test{color:red/*test*/}").use(writer).process();
        assertThat(writer.write()).isEqualTo(".test{color:red/*test*/}");
    }

    @Test
    public void writeToAppendable() throws IOException {
        StyleWriter writer = StyleWriter.compressed();
        StringBuilder builder = new StringBuilder();
        Omakase.source(".test{color:red}").use(writer).process();
        writer.writeTo(builder);
        assertThat(builder.toString()).isEqualTo(".test{color:red}");
    }

    @Test
    public void writeUnitHasOverride() {
        StyleWriter writer = StyleWriter.compressed();
        SampleCustomWriter sample = new SampleCustomWriter();
        writer.override(Selector.class, sample);
        Omakase.source(".test{color:red}").use(writer).process();
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

    @Test
    public void writeSingle() {
        ClassSelector s = new ClassSelector("test");
        assertThat(StyleWriter.writeSingle(s)).isEqualTo(".test");
    }

    private static final class Level1 extends AbstractSyntax {
        SyntaxCollection<Level1, Level2> children = new LinkedSyntaxCollection<>(this);
        int count;

        public Level1() {
            children.append(new Level2(this));
            children.append(new Level2(this));
        }

        @Override
        public Syntax copy() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            assertThat(writer.countAtCurrentDepth()).isEqualTo(0);
            for (Level2 child : children) {
                writer.writeInner(child, appendable);
                count++;
            }
        }
    }

    private static final class Level2 extends AbstractGroupable<Level1, Level2> {
        Level1 parent;

        public Level2(Level1 parent) {
            this.parent = parent;
        }

        @Override
        protected Level2 self() {
            return this;
        }

        @Override
        public Syntax copy() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            assertThat(writer.countAtCurrentDepth() == parent.count);
            if (parent.count == 0) {
                assertThat(writer.isFirstAtCurrentDepth()).isTrue();
            } else {
                assertThat(writer.isFirstAtCurrentDepth()).isFalse();
            }
        }
    }

    @Test
    public void testDepthMethods() throws IOException {
        StyleWriter.verbose().writeInner(new Level1(), new StyleAppendable());
    }

    public static final class SampleCustomWriter implements CustomWriter<Selector> {
        boolean called;

        @Override
        public void write(Selector unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            called = true;
        }
    }
}
