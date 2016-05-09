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

import com.salesforce.omakase.error.OmakaseException;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Comment}. */
@SuppressWarnings("JavaDoc")
public class CommentTest {
    @org.junit.Rule public final org.junit.rules.ExpectedException exception = ExpectedException.none();

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
        assertThat(writer.writeSnippet(c)).isEqualTo("/* test */");
    }

    @Test
    public void writeCompressed() throws IOException {
        Comment c = new Comment(" test ");
        StyleWriter writer = StyleWriter.compressed();
        assertThat(writer.writeSnippet(c)).isEqualTo("/* test */");
    }

    @Test
    public void hasAnnotationTrueSpaced() {
        Comment c = new Comment(" @test");
        assertThat(c.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationFalseCommentNotAnnotated() {
        Comment c = new Comment("test");
        assertThat(c.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationFalseDifferentAnnotation() {
        Comment c = new Comment("@test2");
        assertThat(c.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationFalseDoesntStartWithAnnotation() {
        Comment c = new Comment("test @test");
        assertThat(c.hasAnnotation("test")).isFalse();
    }

    @Test
    public void getAnnotationPresent() {
        Comment c = new Comment("@test");
        assertThat(c.annotation().get().name()).isEqualTo("test");
    }

    @Test
    public void getAnnotationAbsent() {
        Comment c = new Comment("test");
        assertThat(c.annotation().isPresent()).isFalse();
    }

    @Test
    public void getAnnotationByNamePresentNoArgs() {
        Comment c = new Comment("@test");
        CssAnnotation a = c.annotation("test").get();
        assertThat(a.rawArgs().isPresent()).isFalse();
    }
    @Test
    public void getAnnotationByNamePresentWithSpacesNoArgs() {
        Comment c = new Comment("  @test  ");
        CssAnnotation a = c.annotation("test").get();
        assertThat(a.name()).isEqualTo("test");
        assertThat(a.rawArgs().isPresent()).isFalse();
    }

    @Test
    public void getAnnotationByNamePresentMultipleArgs() {
        Comment c = new Comment("@test one two");
        CssAnnotation a = c.annotation("test").get();
        assertThat(a.rawArgs().get()).isEqualTo("one two");
    }

    @Test
    public void getAnnotationByNameAbsentNoAnnotation() {
        Comment c = new Comment("test");
        assertThat(c.annotation("test").isPresent()).isFalse();
    }

    @Test
    public void getAnnotationByNameAbsentDifferentAnnotation() {
        Comment c = new Comment("@test2");
        assertThat(c.annotation("test").isPresent()).isFalse();
    }

    @Test
    public void newCommentFromAnnotation() {
        CssAnnotation a = new CssAnnotation("test");
        Comment c = new Comment(a);
        assertThat(c.content()).isEqualTo("@test");
    }

    @Test
    public void newCommentFromAnnotationWithArgs() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.content()).isEqualTo("@test arg");
    }

    @Test
    public void fromAnnotationObjectHasAnnotationStringTrue() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.hasAnnotation("test")).isTrue();
    }

    @Test
    public void fromAnnotationObjectHasAnnotationStringFalse() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.hasAnnotation("test2")).isFalse();
    }

    @Test
    public void fromAnnotationObjectHasAnnotationObjectTrue() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.hasAnnotation(a)).isTrue();
    }

    @Test
    public void fromAnnotationObjectHasAnnotationDifferentInstance() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.hasAnnotation(new CssAnnotation("test", "arg"))).isTrue();
    }

    @Test
    public void fromAnnotationObjectHasAnnotationObjectFalse() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.hasAnnotation(new CssAnnotation("blah"))).isFalse();
    }

    @Test
    public void fromAnnotationObjectGetAnnotationByNamePresent() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.annotation("test").get()).isSameAs(a);
    }

    @Test
    public void fromAnnotationObjectGetAnnotationByNameAbsent() {
        CssAnnotation a = new CssAnnotation("test", "arg");
        Comment c = new Comment(a);
        assertThat(c.annotation("tes2t").isPresent()).isFalse();
    }

    @Test
    public void startsWithBangTrue() {
        Comment c = new Comment("!copyright");
        assertThat(c.startsWithBang()).isTrue();
    }

    @Test
    public void startsWithBangFalse() {
        Comment c = new Comment("blah!blah!");
        assertThat(c.startsWithBang()).isFalse();
    }
}
