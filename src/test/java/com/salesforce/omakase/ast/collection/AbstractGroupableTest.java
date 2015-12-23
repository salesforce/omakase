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

package com.salesforce.omakase.ast.collection;

import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AbstractGroupable}. */
@SuppressWarnings("JavaDoc")
public class AbstractGroupableTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private Parent parent;
    private Child child1;
    private Child child2;
    private Child child3;

    @Before
    public void before() {
        parent = new Parent();
        child1 = new Child("1");
        child2 = new Child("2");
        child3 = new Child("3");
    }

    @Test
    public void isFirstTrue() {
        parent.collection.append(child1).append(child2).append(child3);
        assertThat(child1.isFirst()).isTrue();
    }

    @Test
    public void isFirstFalse() {
        parent.collection.append(child1).append(child2).append(child3);
        assertThat(child2.isFirst()).isFalse();
        assertThat(child3.isFirst()).isFalse();
    }

    @Test
    public void isFirstTrueWhenNoGroup() {
        assertThat(child1.isFirst()).isTrue();
        assertThat(child2.isFirst()).isTrue();
        assertThat(child3.isFirst()).isTrue();
    }

    @Test
    public void isLastTrue() {
        parent.collection.append(child1).append(child2).append(child3);
        assertThat(child3.isLast()).isTrue();
    }

    @Test
    public void isLastFalse() {
        parent.collection.append(child1).append(child2).append(child3);
        assertThat(child1.isLast()).isFalse();
        assertThat(child2.isLast()).isFalse();
    }

    @Test
    public void isLastTrueWhenNoGroup() {
        assertThat(child1.isLast()).isTrue();
        assertThat(child2.isLast()).isTrue();
        assertThat(child3.isLast()).isTrue();
    }

    @Test
    public void getPrevious() {
        parent.collection.append(child1).append(child2);
        assertThat(child2.previous().get()).isSameAs(child1);
    }

    @Test
    public void previousWhenNotGrouped() {
        assertThat(child1.previous().isPresent()).isFalse();
    }

    @Test
    public void getNext() {
        parent.collection.append(child1).append(child2);
        assertThat(child1.next().get()).isSameAs(child2);
    }

    @Test
    public void nextWhenNotGrouped() {
        assertThat(child1.next().isPresent()).isFalse();
    }

    @Test
    public void prepend() {
        parent.collection.append(child3).append(child1);
        child1.prepend(child2);
        assertThat(parent.collection).containsExactly(child3, child2, child1);
    }

    @Test
    public void prependToUngrouped() {
        exception.expect(IllegalStateException.class);
        child1.prepend(child3);
    }

    @Test
    public void prependToDestroyed() {
        parent.collection.append(child1);
        child1.destroy();
        exception.expect(IllegalStateException.class);
        child1.prepend(child3);
    }

    @Test
    public void prependExistingInSameCollection() {
        parent.collection.append(child1).append(child2).append(child3);
        child1.prepend(child3);
        assertThat(parent.collection).containsExactly(child3, child1, child2);
    }

    @Test
    public void prependExistingInAnotherCollection() {
        parent.collection.append(child1);
        Parent parent2 = new Parent();
        parent2.collection.append(child2).append(child3);
        child2.prepend(child1);
        assertThat(parent.collection).isEmpty();
        assertThat(parent2.collection).containsExactly(child1, child2, child3);
    }

    @Test
    public void prependToItself() {
        parent.collection.append(child1);
        child1.prepend(child1);
        assertThat(parent.collection).containsExactly(child1);
    }

    @Test
    public void append() {
        parent.collection.append(child3).append(child1);
        child1.append(child2);
        assertThat(parent.collection).containsExactly(child3, child1, child2);
    }

    @Test
    public void appendToUngrouped() {
        exception.expect(IllegalStateException.class);
        child1.append(child3);
    }

    @Test
    public void appendToDestroyed() {
        parent.collection.append(child1);
        child1.destroy();
        exception.expect(IllegalStateException.class);
        child1.append(child3);
    }

    @Test
    public void appendExistingInSameCollection() {
        parent.collection.append(child1).append(child2).append(child3);
        child1.prepend(child3);
        assertThat(parent.collection).containsExactly(child3, child1, child2);
    }

    @Test
    public void appendExistingInAnotherCollection() {
        parent.collection.append(child1);
        Parent parent2 = new Parent();
        parent2.collection.append(child2).append(child3);

        child2.append(child1);

        assertThat(parent.collection).isEmpty();
        assertThat(parent2.collection).containsExactly(child2, child1, child3);
    }

    @Test
    public void appendToItself() {
        parent.collection.append(child1);
        child1.append(child1);
        assertThat(parent.collection).containsExactly(child1);
    }

    @Test
    public void replaceWith() {
        parent.collection.append(child1);
        child1.replaceWith(child2);
        assertThat(parent.collection).containsExactly(child2);
    }

    @Test
    public void groupNotPresent() {
        assertThat(child1.group()).isNull();
    }

    @Test
    public void dynamicallyCreatedInitiallyNoGroup() {
        assertThat(new Child("c").group()).isNull();
    }

    @Test
    public void group() {
        parent.collection.append(child1).append(child2).append(child3);
        assertThat(child2.group()).isSameAs(parent.collection);
    }

    @Test
    public void parentWhenGrouped() {
        parent.collection.append(child1).append(child2);
        assertThat(child1.parent()).isSameAs(parent);
    }

    @Test
    public void unlink() {
        parent.collection.append(child1);
        child1.unlink();
        assertThat(parent.collection).doesNotContain(child1);
        assertThat(child1.group()).isNull();
        assertThat(child1.isDestroyed()).isFalse();
    }

    @Test
    public void destroyed() {
        parent.collection.append(child1);
        assertThat(child1.isDestroyed()).isFalse();
        child1.destroy();
        assertThat(child1.isDestroyed()).isTrue();
        assertThat(child1.group()).isNull();
    }

    @Test
    public void destroyAlreadyDestroyed() {
        child1.destroy();
        assertThat(child1.isDestroyed()).isTrue();
    }

    @Test
    public void dynamicallyCreatedNotInitiallyDestroyed() {
        assertThat(new Child("c").isDestroyed()).isFalse();
    }

    @Test
    public void destroyedStatusChanged() {
        child1.destroy();
        assertThat(child1.status()).isSameAs(Status.NEVER_EMIT);
    }

    @Test
    public void writableByDefault() {
        parent.collection.append(child1);
        assertThat(child1.isWritable()).isTrue();
    }

    @Test
    public void writableWhenNotGrouped() {
        assertThat(child1.isWritable()).isTrue();
    }

    @Test
    public void notWritableWhenDestroyed() {
        child1.destroy();
        assertThat(child1.isWritable()).isFalse();
    }

    @Test
    public void parentWhenNotGrouped() {
        assertThat(child1.parent()).isNull();
    }

    private static final class Parent {
        private final SyntaxCollection<Parent, Child> collection = new LinkedSyntaxCollection<>(this);
    }

    private static final class Child extends AbstractGroupable<Parent, Child> {
        private final String name;

        public Child(String name) {
            this.name = name;
        }

        @Override
        protected Child self() {
            return this;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            //noop
        }

        @Override
        public Child copy() {
            throw new UnsupportedOperationException();
        }
    }
}
