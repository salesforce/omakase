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
import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.data.Prefix;
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

    public static final class TestSyntax extends AbstractSyntax<TestSyntax> {
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
        protected TestSyntax makeCopy(Prefix prefix, SupportMatrix support) {
            return new TestSyntax(name);
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}
    }
}
