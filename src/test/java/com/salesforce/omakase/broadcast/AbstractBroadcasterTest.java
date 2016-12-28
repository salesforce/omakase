/*
 * Copyright (c) 2017, salesforce.com, inc.
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

import com.salesforce.omakase.ast.selector.ClassSelector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link AbstractBroadcaster}
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class AbstractBroadcasterTest {
    @Test
    public void chain() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();
        t1.chain(t2);

        t1.broadcast(new ClassSelector("foo"));
        assertThat(t1.called).isEqualTo(1);
        assertThat(t2.called).isEqualTo(1);
    }

    @Test
    public void chainGoesToBottom() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();
        TestBroadcaster t3 = new TestBroadcaster();

        List<TestBroadcaster> orderTracker = new ArrayList<>();
        t1.orderTracker(orderTracker);
        t2.orderTracker(orderTracker);
        t3.orderTracker(orderTracker);

        t1.chain(t2);
        t1.chain(t3);

        t1.broadcast(new ClassSelector("foo"));

        assertThat(orderTracker).hasSize(3);
        assertThat(orderTracker.get(0)).isSameAs(t1);
        assertThat(orderTracker.get(1)).isSameAs(t2);
        assertThat(orderTracker.get(2)).isSameAs(t3);
    }

    @Test
    public void cut() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();
        t1.chain(t2);

        t1.broadcast(new ClassSelector("foo"));
        assertThat(t2.called).isEqualTo(1);

        t1.cut(t2);
        t1.broadcast(new ClassSelector("foo"));
        assertThat(t1.called).isEqualTo(2);
        assertThat(t2.called).isEqualTo(1);
    }

    @Test
    public void cutFromBottom() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();
        TestBroadcaster t3 = new TestBroadcaster();

        t1.chain(t2);
        t1.chain(t3);

        t1.cut(t3);

        t1.broadcast(new ClassSelector("foo"));

        assertThat(t1.called).isEqualTo(1);
        assertThat(t2.called).isEqualTo(1);
        assertThat(t3.called).isEqualTo(0);
    }

    @Test
    public void cutNotPresent() {
        TestBroadcaster t1 = new TestBroadcaster();
        t1.cut(new TestBroadcaster()); // no error
    }

    @Test
    public void chainBroadcastOne() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();

        t1.chainBroadcast(new ClassSelector("test"), t2);

        assertThat(t1.called).isEqualTo(1);
        assertThat(t2.called).isEqualTo(1);

        t1.broadcast(new ClassSelector("test"));

        assertThat(t1.called).isEqualTo(2);
        assertThat(t2.called).isEqualTo(1);
    }

    @Test
    public void chainBroadcastMulti() {
        TestBroadcaster t1 = new TestBroadcaster();
        TestBroadcaster t2 = new TestBroadcaster();
        TestBroadcaster t3 = new TestBroadcaster();
        TestBroadcaster t4 = new TestBroadcaster();

        t1.chainBroadcast(new ClassSelector("test"), t2, t3, t4);

        assertThat(t1.called).isEqualTo(1);
        assertThat(t2.called).isEqualTo(1);
        assertThat(t3.called).isEqualTo(1);
        assertThat(t4.called).isEqualTo(1);

        t1.broadcast(new ClassSelector("test"));

        assertThat(t1.called).isEqualTo(2);
        assertThat(t2.called).isEqualTo(1);
        assertThat(t3.called).isEqualTo(1);
        assertThat(t4.called).isEqualTo(1);
    }

    private static final class TestBroadcaster extends AbstractBroadcaster {
        int called;
        List<TestBroadcaster> orderTracker;

        public void orderTracker(List<TestBroadcaster> orderTracker) {
            this.orderTracker = orderTracker;
        }

        @Override
        public void broadcast(Broadcastable broadcastable) {
            called++;
            if (orderTracker != null) {
                this.orderTracker.add(this);
            }
            relay(broadcastable);
        }
    }
}