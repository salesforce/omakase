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

package com.salesforce.omakase.ast.collection;

import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.test.StatusChangingBroadcaster;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link StandardSyntaxCollection}. */
@SuppressWarnings("JavaDoc")
public class StandardSyntaxCollectionTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private SyntaxCollection<Parent, Child> collection;
    private Child child1;
    private Child child2;
    private Child child3;

    @Before
    public void before() {
        collection = new Parent().collection;
        child1 = new Child();
        child2 = new Child();
        child3 = new Child();
    }

    @Test
    public void size() {
        assertThat(collection.size()).isEqualTo(0);
        collection.append(child1);
        assertThat(collection.size()).isEqualTo(1);
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
    public void isEmptyOrAllDetachedTrue() {
        assertThat(collection.isEmptyOrAllDetached()).isTrue();
        collection.append(child1);
        child1.detach();
        assertThat(collection.isEmptyOrAllDetached()).isTrue();
    }

    @Test
    public void isEmptyOrAllDetachedFalse() {
        collection.append(child1);
        assertThat(collection.isEmptyOrAllDetached()).isFalse();
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
    public void detachUnitInCollection() {
        collection.append(child1);
        collection.detach(child1);
        assertThat(collection).isEmpty();
        assertThat(child1.group().isPresent()).isFalse();
        assertThat(child1.parent().isPresent()).isFalse();
    }

    @Test
    public void detachUnitNotInAnyCollection() {
        exception.expect(IllegalArgumentException.class);
        collection.detach(child1);
    }

    @Test
    public void detachUnitInAnotherCollection() {
        Parent parent2 = new Parent();
        parent2.collection.append(child1);

        exception.expect(IllegalArgumentException.class);
        collection.detach(child1);
        assertThat(child1.isDetached()).isFalse();
    }

    @Test
    public void clear() {
        collection.append(child1).append(child2);
        Iterable<Child> cleared = collection.clear();
        assertThat(cleared).containsExactly(child1, child2);
        assertThat(collection).isEmpty();
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
        private final SyntaxCollection<Parent, Child> collection = StandardSyntaxCollection.create(this);
    }

    private static final class Child extends AbstractGroupable<Parent, Child> implements Syntax {
        @Override
        protected Child self() {
            return this;
        }

        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
            //noop
        }
    }
}
