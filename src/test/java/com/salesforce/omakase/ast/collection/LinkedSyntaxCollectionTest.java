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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.test.functional.StatusChangingBroadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link LinkedSyntaxCollection}. */
@SuppressWarnings("JavaDoc")
public class LinkedSyntaxCollectionTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private SyntaxCollection<Parent, Child> collection;
    private Child child1;
    private Child child2;
    private Child child3;

    @Before
    public void before() {
        collection = new Parent().collection;
        child1 = new Child(1);
        child2 = new Child(2);
        child3 = new Child(3);
    }

    @Test
    public void size() {
        assertThat(collection.size()).isEqualTo(0);
        collection.append(child1);
        assertThat(collection.size()).isEqualTo(1);
        collection.append(child2);
        assertThat(collection.size()).isEqualTo(2);
    }

    @Test
    public void isEmptyTrue() {
        assertThat(collection.isEmpty()).isTrue();
    }

    @Test
    public void isEmptyFalse() {
        collection.append(child1);
        assertThat(collection.isEmpty()).isFalse();
    }

    @Test
    public void isEmptyAfterRemove() {
        collection.append(child1);
        collection.remove(child1);
        assertThat(collection.isEmpty()).isTrue();
    }

    @Test
    public void isEmptyOrNoneWritableTrueWhenEmpty() {
        assertThat(collection.isEmpty()).isTrue();
        assertThat(collection.isEmptyOrNoneWritable()).isTrue();
    }

    @Test
    public void isEmptyOrNonWritableFalse() {
        collection.append(child1);
        assertThat(child1.isWritable()).isTrue();
        assertThat(collection.isEmptyOrNoneWritable()).isFalse();
    }

    @Test
    public void isEmptyOrNoneWritableTrue() {
        SyntaxCollection<Parent, ChildNotWritable> c = new LinkedSyntaxCollection<>(new Parent());
        c.append(new ChildNotWritable());
        assertThat(collection.isEmptyOrNoneWritable()).isTrue();
    }

    @Test
    public void containsTrue() {
        collection.append(child1).append(child3);
        assertThat(collection.contains(child3)).isTrue();
    }

    @Test
    public void containsFalse() {
        collection.append(child1).append(child3);
        assertThat(collection.contains(child2)).isFalse();
    }

    @Test
    public void getFirstWhenMultiple() {
        collection.append(child1).append(child2).append(child3);
        assertThat(collection.first().get()).isSameAs(child1);
    }

    @Test
    public void getFirstWhenEmpty() {
        assertThat(collection.first().isPresent()).isFalse();
    }

    @Test
    public void getLastWhenMultiple() {
        collection.append(child1).append(child2).append(child3);
        assertThat(collection.last().get()).isSameAs(child3);
    }

    @Test
    public void getLastWhenEmpty() {
        assertThat(collection.last().isPresent()).isFalse();
    }

    @Test
    public void nextPresent() {
        collection.append(child1);
        collection.append(child2);
        assertThat(collection.next(child1).get()).isSameAs(child2);
    }

    @Test
    public void nextAbsent() {
        collection.append(child1);
        collection.append(child2);
        assertThat(collection.next(child2).isPresent()).isFalse();
    }

    @Test
    public void errorsIfNextNotPresent() {
        exception.expect(IllegalArgumentException.class);
        collection.next(child1);
    }

    @Test
    public void previousPresent() {
        collection.append(child1);
        collection.append(child2);
        assertThat(collection.previous(child2).get()).isSameAs(child1);
    }

    @Test
    public void previousAbsent() {
        collection.append(child1);
        collection.append(child2);
        assertThat(collection.previous(child1).isPresent()).isFalse();
    }

    @Test
    public void errorsIfPreviousNotPresent() {
        exception.expect(IllegalArgumentException.class);
        collection.previous(child1);
    }

    @Test
    public void findPresent() {
        Selector s = new Selector();
        ClassSelector part1 = new ClassSelector("test");
        IdSelector part2 = new IdSelector("test");
        s.parts().append(part1).append(part2);

        Optional<IdSelector> found = s.parts().find(IdSelector.class);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isSameAs(part2);
    }

    @Test
    public void findAbsent() {
        Selector s = new Selector();
        ClassSelector part1 = new ClassSelector("test");
        IdSelector part2 = new IdSelector("test");
        s.parts().append(part1).append(part2);

        Optional<PseudoClassSelector> found = s.parts().find(PseudoClassSelector.class);
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void prependedIsFirst() {
        collection.append(child1).append(child2);
        collection.prepend(child3);
        assertThat(collection.first().get()).isSameAs(child3);
    }

    @Test
    public void prependingUnbroadcastedGetsBroadcasted() {
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        assertThat(child1.status()).isSameAs(Status.UNBROADCASTED);
        collection.prepend(child1);
        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void prependAll() {
        collection.append(child3);
        collection.prependAll(Lists.newArrayList(child2, child1));

        assertThat(collection).containsExactly(child2, child1, child3);
    }

    @Test
    public void forPrependAllEachUnbroadcastedGetsBroadcasted() {
        collection.append(child3);
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        collection.prependAll(Lists.newArrayList(child2, child1));

        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
        assertThat(child2.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void prependExisting() {
        collection.prepend(child1);
        collection.prepend(child1);
        assertThat(collection).containsExactly(child1);
        assertThat(child1.group()).isSameAs(collection);
    }

    @Test
    public void prependExistingMultiple() {
        collection.append(child1).append(child2).append(child3);
        collection.prepend(child3);
        assertThat(collection).containsExactly(child3, child1, child2);
    }

    @Test
    public void prependFromAnotherGroup() {
        SyntaxCollection<Parent, Child> collection2 = new Parent().collection;
        collection2.prepend(child1);
        collection.prepend(child1);
        assertThat(collection).containsExactly(child1);
        assertThat(child1.group()).isSameAs(collection);
        assertThat(collection2).isEmpty();
    }

    @Test
    public void prependBefore() {
        collection.append(child1).append(child2);
        collection.prependBefore(child2, child3);
        assertThat(collection).containsExactly(child1, child3, child2);
    }

    @Test
    public void prependBeforeResultingInFirstPosition() {
        collection.append(child1).append(child2);
        collection.prependBefore(child1, child3);
        assertThat(collection).containsExactly(child3, child1, child2);
    }

    @Test
    public void prependBeforeUnbroadcastedGetsBroadcasted() {
        collection.append(child1).append(child2);
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        collection.prependBefore(child1, child3);
        assertThat(child3.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void prependBeforeNotInCollection() {
        exception.expect(IllegalArgumentException.class);
        collection.prependBefore(child3, child1);
    }

    @Test
    public void prependBeforeItself() {
        collection.append(child1);
        collection.prependBefore(child1, child1);
        assertThat(collection).containsExactly(child1);
    }

    @Test
    public void prependBeforeIsFirst() {
        collection.append(child1).append(child2);
        collection.prependBefore(child2, child1);
        assertThat(collection).containsExactly(child1, child2);
    }

    @Test
    public void prependBeforeIsLast() {
        collection.append(child1).append(child2);
        collection.prependBefore(child1, child2);
        assertThat(collection).containsExactly(child2, child1);
    }

    @Test
    public void appendedIsLast() {
        collection.append(child1).append(child2);
        collection.append(child3);
        assertThat(collection.last().get()).isSameAs(child3);
    }

    @Test
    public void appendingUnbroadcastedGetsBroadcasted() {
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        assertThat(child1.status()).isSameAs(Status.UNBROADCASTED);
        collection.append(child1);
        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void appendAll() {
        collection.append(child3);
        collection.appendAll(Lists.newArrayList(child2, child1));

        assertThat(collection).hasSize(3);
        assertThat(collection).containsExactly(child3, child2, child1);
    }

    @Test
    public void forAppendAllEachUnbroadcastedGetsBroadcasted() {
        collection.append(child3);
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        collection.appendAll(Lists.newArrayList(child2, child1));

        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
        assertThat(child2.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void appendExisting() {
        collection.append(child1);
        collection.append(child1);
        assertThat(collection).containsExactly(child1);
        assertThat(child1.group()).isSameAs(collection);
    }

    @Test
    public void appendExistingMultiple() {
        collection.append(child1).append(child2).append(child3);
        collection.append(child1);
        assertThat(collection).containsExactly(child2, child3, child1);
    }

    @Test
    public void appendFromAnotherGroup() {
        SyntaxCollection<Parent, Child> collection2 = new Parent().collection;
        collection2.append(child1);
        collection.append(child1);
        assertThat(collection).containsExactly(child1);
        assertThat(child1.group()).isSameAs(collection);
        assertThat(collection2).isEmpty();
    }

    @Test
    public void appendAfter() {
        collection.append(child1).append(child2);
        collection.appendAfter(child1, child3);
        assertThat(collection).containsExactly(child1, child3, child2);
    }

    @Test
    public void appendAfterResultingInLast() {
        collection.append(child1).append(child2);
        collection.appendAfter(child2, child3);
        assertThat(collection).containsExactly(child1, child2, child3);
    }

    @Test
    public void appendAfterUnbroadcastedGetsBroadcasted() {
        collection.append(child1).append(child2);
        collection.propagateBroadcast(new StatusChangingBroadcaster());
        collection.appendAfter(child1, child3);
        assertThat(child3.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void appendAfterNotInCollection() {
        exception.expect(IllegalArgumentException.class);
        collection.appendAfter(child3, child1);
    }

    @Test
    public void appendAfterItself() {
        collection.append(child1);
        collection.appendAfter(child1, child1);
        assertThat(collection).containsExactly(child1);
    }

    @Test
    public void appendAfterIsFirst() {
        collection.append(child1).append(child2);
        collection.appendAfter(child1, child2);
        assertThat(collection).containsExactly(child1, child2);
    }

    @Test
    public void appendAfterIsLast() {
        collection.append(child1).append(child2);
        collection.appendAfter(child2, child1);
        assertThat(collection).containsExactly(child2, child1);
    }

    @Test
    public void prependDestroyed() {
        child1.destroy();
        exception.expect(IllegalArgumentException.class);
        collection.prepend(child1);
    }

    @Test
    public void appendDestroyed() {
        child1.destroy();
        exception.expect(IllegalArgumentException.class);
        collection.append(child1);
    }

    @Test
    public void prependBeforeDestroyed() {
        collection.append(child1);
        child2.destroy();
        exception.expect(IllegalArgumentException.class);
        collection.prependBefore(child1, child2);
    }

    @Test
    public void appendAfterDestroyed() {
        collection.append(child1);
        child2.destroy();
        exception.expect(IllegalArgumentException.class);
        collection.appendAfter(child1, child2);
    }

    @Test
    public void replaceExistingWithWhenEmpty() {
        collection.replaceExistingWith(Lists.newArrayList(child1, child2));
        assertThat(collection).containsExactly(child1, child2);
    }

    @Test
    public void replaceExistinWithWhenNotEmpty() {
        collection.append(child3);
        collection.replaceExistingWith(Lists.newArrayList(child1, child2));
        assertThat(collection).containsExactly(child1, child2);
    }


    @Test
    public void replaceExistinWithSingle() {
        collection.append(child3);
        collection.replaceExistingWith(child1);
        assertThat(collection).containsExactly(child1);
    }
    @Test
    public void removeUnitInCollection() {
        collection.append(child1);
        collection.remove(child1);
        assertThat(collection).isEmpty();
        assertThat(child1.group()).isNull();
        assertThat(child1.parent()).isNull();
    }

    @Test
    public void removeUnitNotInAnyCollection() {
        collection.append(child2);
        collection.remove(child1);
        assertThat(collection).containsExactly(child2);
    }

    @Test
    public void removeUnitInAnotherCollection() {
        Parent parent2 = new Parent();
        parent2.collection.append(child1);

        collection.remove(child1);
        assertThat(parent2.collection).containsExactly(child1);
    }

    @Test
    public void clear() {
        collection.append(child1).append(child2);
        assertThat(collection.clear()).isEmpty();
    }

    @Test
    public void parent() {
        Parent p = new Parent();
        assertThat(p.collection.parent()).isSameAs(p);
    }

    @Test
    public void propagatesBroadcast() {
        collection.append(child1);
        assertThat(child1.status()).isSameAs(Status.UNBROADCASTED);
        StatusChangingBroadcaster broadcaster = new StatusChangingBroadcaster();
        collection.propagateBroadcast(broadcaster);
        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void propagateBroadcastSavesTheBroadcaster() {
        StatusChangingBroadcaster broadcaster = new StatusChangingBroadcaster();
        collection.propagateBroadcast(broadcaster);
        collection.append(child1);
        assertThat(child1.status()).isNotSameAs(Status.UNBROADCASTED);
    }

    private static final class Parent {
        private final SyntaxCollection<Parent, Child> collection = new LinkedSyntaxCollection<>(this);
    }

    private static final class Child extends AbstractGroupable<Parent, Child> {
        private final int i;

        public Child(int i) {
            this.i = i;
        }

        @Override
        protected Child self() {
            return this;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public Child copy() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ChildNotWritable extends AbstractGroupable<Parent, ChildNotWritable> {
        @Override
        protected ChildNotWritable self() {
            return this;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public ChildNotWritable copy() {
            throw new UnsupportedOperationException();
        }
    }
}
