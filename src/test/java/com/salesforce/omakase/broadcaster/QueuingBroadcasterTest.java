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

package com.salesforce.omakase.broadcaster;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link QueuingBroadcaster}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class QueuingBroadcasterTest {
    private QueuingBroadcaster queue;
    private QueryableBroadcaster qb;
    private Syntax unit;

    @Before
    public void setup() {
        qb = new QueryableBroadcaster();
        queue = new QueuingBroadcaster(qb);
        unit = new ClassSelector("test");
    }

    @Test
    public void broadcastWhenNotPaused() {
        queue.broadcast(unit);
        assertThat(qb.all()).hasSize(1);
    }

    @Test
    public void broadcastWhenPaused() {
        queue.pause();
        queue.broadcast(unit);
        assertThat(qb.all()).isEmpty();
    }

    @Test
    public void resume() {
        queue.pause();
        queue.broadcast(unit);
        assertThat(queue.all()).hasSize(1);

        queue.resume();
        assertThat(queue.all()).isEmpty();
        assertThat(qb.all()).hasSize(1);
    }

    @Test
    public void broadcastOrderWhenResumingAfterPause() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");
        Syntax u4 = new ClassSelector("test4");
        Syntax u5 = new ClassSelector("test5");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);
        queue.broadcast(u4);
        queue.broadcast(u5);

        queue.resume();

        assertThat(Iterables.get(qb.all(), 0)).isSameAs(u1);
        assertThat(Iterables.get(qb.all(), 1)).isSameAs(u2);
        assertThat(Iterables.get(qb.all(), 2)).isSameAs(u3);
        assertThat(Iterables.get(qb.all(), 3)).isSameAs(u4);
        assertThat(Iterables.get(qb.all(), 4)).isSameAs(u5);
    }

    @Test
    public void peek() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        assertThat(queue.peek()).isSameAs(u1);
    }

    @Test
    public void peekLast() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        assertThat(queue.peekLast()).isSameAs(u3);
    }

    @Test
    public void rejectFirst() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        queue.reject(u1);

        queue.resume();

        assertThat(qb.all()).hasSize(2);
        assertThat(Iterables.get(qb.all(), 0)).isSameAs(u2);
        assertThat(Iterables.get(qb.all(), 1)).isSameAs(u3);
    }

    @Test
    public void rejectLast() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        queue.reject(u3);

        queue.resume();

        assertThat(qb.all()).hasSize(2);
        assertThat(Iterables.get(qb.all(), 0)).isSameAs(u1);
        assertThat(Iterables.get(qb.all(), 1)).isSameAs(u2);
    }

    @Test
    public void rejectMiddle() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        queue.reject(u2);

        queue.resume();

        assertThat(qb.all()).hasSize(2);
        assertThat(Iterables.get(qb.all(), 0)).isSameAs(u1);
        assertThat(Iterables.get(qb.all(), 1)).isSameAs(u3);
    }

    @Test
    public void rejectedListRemovedAfterResuming() {
        queue.pause();
        queue.broadcast(unit);
        queue.reject(unit);
        queue.resume();
        assertThat(qb.all()).isEmpty();

        queue.pause();
        queue.broadcast(unit);
        queue.resume();
        assertThat(qb.all()).hasSize(1);
    }

    @Test
    public void size() {
        queue.pause();

        Syntax u1 = new ClassSelector("test1");
        Syntax u2 = new ClassSelector("test2");
        Syntax u3 = new ClassSelector("test3");

        queue.broadcast(u1);
        queue.broadcast(u2);
        queue.broadcast(u3);

        assertThat(queue.size()).isEqualTo(3);
    }
}
