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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AbstractSyntax}. */
@SuppressWarnings("JavaDoc")
public class AbstractSyntaxTest {
    @Test
    public void testLine() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        assertThat(t.line()).isEqualTo(10);
    }

    @Test
    public void testColumn() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        assertThat(t.column()).isEqualTo(15);
    }

    @Test
    public void testHasSourcePositionTrue() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        assertThat(t.hasSourcePosition()).isTrue();
    }

    @Test
    public void testHasSourcePositionFalse() throws Exception {
        assertThat(new TestSyntax().hasSourcePosition()).isFalse();
    }

    @Test
    public void defaultIsWritable() {
        assertThat(new TestSyntax().isWritable()).isTrue();
    }

    @Test
    public void testCopy() {
        TestSyntax t = new TestSyntax("name");
        t.comments(Lists.newArrayList("comment"));
        t.orphanedComments(Lists.newArrayList("orphaned comment"));
        TestSyntax copy = t.copy();

        assertThat(copy.name).isEqualTo("name");
        assertThat(copy.comments()).hasSize(1);
        assertThat(copy.comments().get(0).content()).isEqualTo("comment");
        assertThat(copy.orphanedComments()).hasSize(1);
        assertThat(copy.orphanedComments().get(0).content()).isEqualTo("orphaned comment");
    }

    @Test
    public void testAddCommentString() {
        TestSyntax t = new TestSyntax(10, 15);
        t.comment("test");
        assertThat(t.comments().get(0).content()).isEqualTo("test");
    }

    @Test
    public void testAddComment() {
        TestSyntax t = new TestSyntax(10, 15);
        Comment c = new Comment("test");
        t.comment(c);
        assertThat(t.comments()).containsExactly(c);
    }

    @Test
    public void testComments() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        t.comments(Lists.newArrayList("my comment"));
        assertThat(t.comments()).hasSize(1);
        assertThat(Iterables.get(t.comments(), 0).content()).isEqualTo("my comment");
    }

    @Test
    public void testCommentsEmpty() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        assertThat(t.comments()).isEmpty();
    }

    @Test
    public void testCommentsFromOtherSyntax() {
        TestSyntax t = new TestSyntax();
        t.comments(Lists.newArrayList("test"));

        TestSyntax t2 = new TestSyntax();
        t2.comments(t);
        assertThat(t.comments().get(0).content()).isEqualTo("test");
    }

    @Test
    public void testCommentsFromDynamicAnnotation() {
        TestSyntax t = new TestSyntax();
        CssAnnotation a = new CssAnnotation("test");

        assertThat(t.comments()).hasSize(0);
        t.annotate(a);
        assertThat(t.comments()).hasSize(1);
    }

    @Test
    public void testOrphanedComments() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        t.orphanedComments(Lists.newArrayList("my comment"));
        assertThat(t.orphanedComments()).hasSize(1);
        assertThat(Iterables.get(t.orphanedComments(), 0).content()).isEqualTo("my comment");
    }

    @Test
    public void testOrphanedCommentsEmpty() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        assertThat(t.orphanedComments()).isEmpty();
    }

    @Test
    public void testOrphanedCommentsFromOtherSyntax() {
        TestSyntax t = new TestSyntax();
        t.orphanedComments(Lists.newArrayList("test"));

        TestSyntax t2 = new TestSyntax();
        t2.orphanedComments(t);
        assertThat(t.orphanedComments().get(0).content()).isEqualTo("test");
    }

    @Test
    public void defaultStatusIsUnbroadcasted() {
        assertThat(new TestSyntax(10, 10).status()).isSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void testStatus() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        t.status(Status.PROCESSED);
        assertThat(t.status()).isSameAs(Status.PROCESSED);
    }

    @Test
    public void testPropagateBroadcastUnbroadcasted() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        t.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).hasSize(1);
    }

    @Test
    public void testPropagateBroadcastAlreadyBroadcasted() throws Exception {
        TestSyntax t = new TestSyntax(10, 15);
        t.status(Status.PROCESSED);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        t.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).isEmpty();
    }

    @Test
    public void hasAnnotationStringNameTrue() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test"));
        assertThat(t.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationStringNameTrueFromDynamicAnnotation() {
        TestSyntax t = new TestSyntax(1, 1);
        t.annotate(new CssAnnotation("test"));
        assertThat(t.hasAnnotation("test")).isTrue();
    }

    @Test
    public void hasAnnotationStringNameFalseNoComments() {
        TestSyntax t = new TestSyntax(1, 1);
        assertThat(t.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationStringNameFalseDifferentComments() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test2"));
        assertThat(t.hasAnnotation("test")).isFalse();
    }

    @Test
    public void hasAnnotationObjectTrueFromNormal() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test"));
        assertThat(t.hasAnnotation(new CssAnnotation("test"))).isTrue();
    }

    @Test
    public void hasAnnotationObjectTrueFromDynamicAnnotation() {
        TestSyntax t = new TestSyntax();
        CssAnnotation a = new CssAnnotation("test");
        t.annotate(a);
        assertThat(t.hasAnnotation(a)).isTrue();
    }

    @Test
    public void hasAnnotationObjectFalse() {
        TestSyntax t = new TestSyntax();
        CssAnnotation a = new CssAnnotation("test");
        t.annotate(a);
        assertThat(t.hasAnnotation(new CssAnnotation("test2"))).isFalse();
    }

    @Test
    public void hasAnnotationObjectFalseNoneAdded() {
        TestSyntax t = new TestSyntax();
        assertThat(t.hasAnnotation(new CssAnnotation("test"))).isFalse();
    }

    @Test
    public void getAnnotationPresent() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test"));
        assertThat(t.annotation("test").isPresent()).isTrue();
    }

    @Test
    public void getAnnotationFromDynamicAnnotationPresent() {
        TestSyntax t = new TestSyntax(1, 1);
        CssAnnotation a = new CssAnnotation("test");
        t.annotate(a);
        assertThat(t.annotation("test").isPresent()).isTrue();
    }

    @Test
    public void getAnnotationFromDynamicAnnotationAbsent() {
        TestSyntax t = new TestSyntax(1, 1);
        CssAnnotation a = new CssAnnotation("test");
        t.annotate(a);
        assertThat(t.annotation("test2").isPresent()).isFalse();
    }

    @Test
    public void getAnnotationAbsentNoComments() {
        TestSyntax t = new TestSyntax(1, 1);
        assertThat(t.annotation("test").isPresent()).isFalse();
    }

    @Test
    public void getAnnotationAbsentDifferentComments() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test2"));
        assertThat(t.annotation("test").isPresent()).isFalse();
    }

    @Test
    public void allAnnotations() {
        TestSyntax t = new TestSyntax(1, 1);
        t.comments(Lists.newArrayList("@test", "blah", "@test2", "aaaa @", "@test3 one"));
        assertThat(t.annotations()).hasSize(3);
    }

    @Test
    public void allAnnotationsWhenEmpty() {
        TestSyntax t = new TestSyntax(1, 1);
        assertThat(t.annotations()).isEmpty();
    }

    @Test
    public void allAnnotationsFromDynamicAnnotation() {
        TestSyntax t = new TestSyntax();
        t.comments(Lists.newArrayList("@test"));
        t.annotate(new CssAnnotation("one"));
        t.annotate(new CssAnnotation("two"));
        assertThat(t.annotations()).hasSize(3);
    }

    @Test
    public void annotateUnlessPresentFromSource() {
        TestSyntax t = new TestSyntax();
        t.comments(Lists.newArrayList("@test"));
        t.annotateUnlessPresent(new CssAnnotation("test"));
        assertThat(t.annotations()).hasSize(1);
    }

    @Test
    public void annotateUnlessPresentTriedTwice() {
        TestSyntax t = new TestSyntax();
        t.annotateUnlessPresent(new CssAnnotation("test"));
        t.annotateUnlessPresent(new CssAnnotation("test"));
        assertThat(t.annotations()).hasSize(1);
    }

    @Test
    public void hasId() {
        TestSyntax t1 = new TestSyntax();
        TestSyntax t2 = new TestSyntax();
        assertThat(t1.id()).isNotEqualTo(t2.id());
    }

    public static final class TestSyntax extends AbstractSyntax {
        private String name;

        public TestSyntax() {
        }

        public TestSyntax(String name) {
            this.name = name;
        }

        public TestSyntax(int line, int column) {
            super(line, column);
        }

        @Override
        public TestSyntax copy() {
            return new TestSyntax(name).copiedFrom(this);
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}
    }

}
