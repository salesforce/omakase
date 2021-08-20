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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;

/**
 * Unit tests for {@link QueuingBroadcaster}.
 *
 * @author nmcwilliams
 */
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
        assertThat(qb.all()).containsExactly(u1, u2, u3, u4, u5);
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
        assertThat(qb.all()).containsExactly(u2, u3);
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
        assertThat(qb.all()).containsExactly(u1, u2);
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
        assertThat(qb.all()).containsExactly(u1, u3);
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
