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

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link AbstractSyntax}. */
@SuppressWarnings("JavaDoc")
public class AbstractSyntaxTest {
    @Test
    public void testLine() throws Exception {
        TestClass t = new TestClass(10, 15);
        assertThat(t.line()).isEqualTo(10);
    }

    @Test
    public void testColumn() throws Exception {
        TestClass t = new TestClass(10, 15);
        assertThat(t.column()).isEqualTo(15);
    }

    @Test
    public void testHasSourcePositionTrue() throws Exception {
        TestClass t = new TestClass(10, 15);
        assertThat(t.hasSourcePosition()).isTrue();
    }

    @Test
    public void testHasSourcePositionFalse() throws Exception {
        assertThat(new TestClass().hasSourcePosition()).isFalse();
    }

    @Test
    public void testComments() throws Exception {
        TestClass t = new TestClass(10, 15);
        t.comments(Lists.newArrayList("my comment"));
        assertThat(t.comments()).hasSize(1);
        assertThat(Iterables.get(t.comments(), 0).content()).isEqualTo("my comment");
    }

    @Test
    public void testCommentsEmpty() throws Exception {
        TestClass t = new TestClass(10, 15);
        assertThat(t.comments()).isEmpty();
    }

    @Test
    public void defaultStatusIsUnbroadcasted() {
        assertThat(new TestClass(10, 10).status()).isSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void testStatus() throws Exception {
        TestClass t = new TestClass(10, 15);
        t.status(Status.BROADCASTED_PREPROCESS);
        assertThat(t.status()).isSameAs(Status.BROADCASTED_PREPROCESS);
    }

    @Test
    public void testBroadcaster() throws Exception {
        TestClass t = new TestClass(10, 15);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        t.broadcaster(broadcaster);
        assertThat(t.broadcaster()).isSameAs(broadcaster);
    }

    @Test
    public void testPropagateBroadcastUnbroadcasted() throws Exception {
        TestClass t = new TestClass(10, 15);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        t.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).hasSize(1);
    }

    @Test
    public void testPropagateBroadcastAlreadyBroadcasted() throws Exception {
        TestClass t = new TestClass(10, 15);
        t.status(Status.BROADCASTED_PREPROCESS);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        t.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).isEmpty();
    }

    public static final class TestClass extends AbstractSyntax {
        public TestClass() {}

        public TestClass(int line, int column) {
            super(line, column);
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {}
    }
}
