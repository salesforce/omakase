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

package com.salesforce.omakase.broadcast;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Status;
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
        @SuppressWarnings("rawtypes")
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

    @Test
    public void updatesStatus() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        sample1.status(Status.UNBROADCASTED);
        qb.broadcast(sample1);
        assertThat(sample1.status()).isSameAs(Status.QUEUED);
    }

    @Test
    public void count() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        qb.broadcast(sample1b);
        assertThat(qb.count()).isEqualTo(3);
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
