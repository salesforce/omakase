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

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.ast.AbstractSyntax;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.LinkedSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.ast.declaration.Declaration;
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
    public void writeWithAllComments() {
        StyleWriter writer = StyleWriter.compressed().writeAllComments(true);
        Omakase.source("/*test*/.test{color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*test*/.test{color:red}");

        Omakase.source("/*@test*/.test{color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*@test*/.test{color:red}");

        Omakase.source("/*!test*/.test{color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*!test*/.test{color:red}");
    }

    @Test
    public void writeWithAnnotatedCommentsOnly() {
        StyleWriter writer = StyleWriter.compressed().writeAnnotatedComments(true);
        Omakase.source("/*@yes*/.test{/*no*//*!no*/color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*@yes*/.test{color:red}");
    }

    @Test
    public void writeWithBangCommentsOnly() {
        StyleWriter writer = StyleWriter.compressed().writeBangComments(true);
        Omakase.source("/*!yes*/.test{/*no*//*@no*/color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*!yes*/.test{color:red}");
    }

    @Test
    public void writeWithAnnotatedCommentsBangAnnotatedComment() {
        StyleWriter writer = StyleWriter.compressed().writeAnnotatedComments(true);
        Omakase.source("/*! @yes*/.test{/*no*//*!no*/color:red}").use(writer).process();
        assertThat(writer.write()).isEqualTo("/*! @yes*/.test{color:red}");
    }

    @Test
    public void writeWithOrphanedComments() {
        StyleWriter writer = StyleWriter.compressed().writeAllComments(true);
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
        CustomSelectorWriter1 sample = new CustomSelectorWriter1();
        writer.addCustomWriter(Selector.class, sample);
        Omakase.source(".test{color:red}").use(writer).process();
        writer.write();
        assertThat(sample.called).isTrue();
    }

    @Test
    public void writeUnitHasSingleOverride() throws IOException {
        StyleWriter writer = StyleWriter.compressed();

        CustomSelectorWriter1 selectorWriter = new CustomSelectorWriter1();
        writer.addCustomWriter(Selector.class, selectorWriter);

        writer.writeSingle(new Selector(new ClassSelector("test")));

        assertThat(selectorWriter.called).isTrue();
    }

    @Test
    public void writeUnitHasMultipleOverridesForSameType() {
        StyleWriter writer = StyleWriter.compressed();

        CustomSelectorWriter1 selectorWriter1 = new CustomSelectorWriter1();
        CustomSelectorWriter2 selectorWriter2 = new CustomSelectorWriter2();
        writer.addCustomWriter(Selector.class, selectorWriter1);
        writer.addCustomWriter(Selector.class, selectorWriter2);

        writer.writeSingle(new Selector(new ClassSelector("test")));

        assertThat(selectorWriter1.called).isTrue();
        assertThat(selectorWriter2.called).isFalse();
    }

    @Test
    public void writeUnitHasMultipleOverridesForDifferentTypes() {
        StyleWriter writer = StyleWriter.compressed();

        CustomDeclarationWriter declarationWriter = new CustomDeclarationWriter();
        CustomSelectorWriter1 selectorWriter = new CustomSelectorWriter1();
        writer.addCustomWriter(Declaration.class, declarationWriter);
        writer.addCustomWriter(Selector.class, selectorWriter);

        writer.writeSingle(new Selector(new ClassSelector("test")));

        assertThat(declarationWriter.called).isFalse();
        assertThat(selectorWriter.called).isTrue();
    }

    @Test
    public void writeUnitHasOverrideThatDoesNothing() {
        StyleWriter writer = StyleWriter.compressed();

        CustomSelectorWriterDoesNothing selectorWriter1 = new CustomSelectorWriterDoesNothing();
        CustomSelectorWriter2 selectorWriter2 = new CustomSelectorWriter2();
        writer.addCustomWriter(Selector.class, selectorWriter1);
        writer.addCustomWriter(Selector.class, selectorWriter2);

        writer.writeSingle(new Selector(new ClassSelector("test")));

        assertThat(selectorWriter2.called).isTrue();
    }

    @Test
    public void writeSingle() {
        ClassSelector s = new ClassSelector("test");
        assertThat(StyleWriter.inline().writeSingle(s)).isEqualTo(".test");
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

    public static final class CustomSelectorWriter1 implements CustomWriter<Selector> {
        boolean called;

        @Override
        public boolean write(Selector unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            called = true;
            return true;
        }
    }

    public static final class CustomSelectorWriter2 implements CustomWriter<Selector> {
        boolean called;

        @Override
        public boolean write(Selector unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            called = true;
            return true;
        }
    }

    public static final class CustomSelectorWriterDoesNothing implements CustomWriter<Selector> {
        @Override
        public boolean write(Selector unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            return false;
        }
    }

    public static final class CustomDeclarationWriter implements CustomWriter<Declaration> {
        boolean called;

        @Override
        public boolean write(Declaration unit, StyleWriter writer, StyleAppendable appendable) throws IOException {
            called = true;
            return true;
        }
    }
}
