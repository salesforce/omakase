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

package com.salesforce.omakase.broadcast;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link QueryableBroadcaster}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class QueryableBroadcasterTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final ClassSelector sample1 = new ClassSelector(1, 1, "test");
    private final ClassSelector sample1a = new ClassSelector(1, 1, "test");
    private final ClassSelector sample1b = new ClassSelector(1, 1, "test");
    private final IdSelector sample2 = new IdSelector(1, 1, "test");

    @Test
    public void filterMatches() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<ClassSelector> filtered = qb.filter(ClassSelector.class);
        assertThat(filtered).hasSize(1);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
    }

    @Test
    public void filterHigherHierarchy() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<Syntax> filtered = qb.filter(Syntax.class);
        assertThat(filtered).hasSize(2);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
        assertThat(Iterables.get(filtered, 1)).isSameAs(sample2);
    }

    @Test
    public void filterDoesntMatch() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<PseudoElementSelector> filtered = qb.filter(PseudoElementSelector.class);
        assertThat(filtered).isEmpty();
    }

    @Test
    public void findExists() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        qb.broadcast(sample1b);
        Optional<ClassSelector> found = qb.find(ClassSelector.class);
        assertThat(found.get()).isSameAs(sample1);
    }

    @Test
    public void findDoesntExist() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        Optional<IdSelector> found = qb.find(IdSelector.class);
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void findOnlyOneMatch() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        Optional<ClassSelector> found = qb.findOnly(ClassSelector.class);
        assertThat(found.isPresent()).isTrue();
        // and no exception
    }

    @Test
    public void findOnlyMoreThanOne() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        exception.expect(IllegalStateException.class);
        exception.expectMessage("expected to find only one broadcasted event");
        qb.findOnly(ClassSelector.class);
    }

    @Test
    public void findOnlyNoMatches() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        Optional<IdSelector> found = qb.findOnly(IdSelector.class);
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void all() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<Broadcastable> filtered = qb.all();
        assertThat(filtered).hasSize(2);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
        assertThat(Iterables.get(filtered, 1)).isSameAs(sample2);
    }

    @Test
    public void relaysToInnerBroadcaster() {
        InnerBroadcaster ib = new InnerBroadcaster();
        QueryableBroadcaster qb = new QueryableBroadcaster(ib);
        qb.broadcast(sample1);
        assertThat(ib.called).isTrue();
    }

    public static final class InnerBroadcaster implements Broadcaster {
        boolean called = false;

        @Override
        public void broadcast(Broadcastable broadcastable) {
            called = true;
        }

        @Override
        public void broadcast(Broadcastable broadcastable, boolean propagate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void wrap(Broadcaster relay) {
            throw new UnsupportedOperationException();
        }
    }
}
