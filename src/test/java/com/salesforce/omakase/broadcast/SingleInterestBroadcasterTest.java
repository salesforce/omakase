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

import org.junit.Test;

import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;

/**
 * Unit tests for {@link SingleInterestBroadcaster}.
 *
 * @author nmcwilliams
 */
public class SingleInterestBroadcasterTest {

    @Test
    public void broadcastedExpectedUnit() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);
        assertThat(sb.one().get()).isSameAs(s);
        assertThat(sb.gather()).containsExactly(s);
    }

    @Test
    public void onlyStoresFirstOccurrence() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        sb.broadcast(s2);
        assertThat(sb.one().get()).isSameAs(s);
    }

    @Test
    public void ignoresBroadcastsOfWrongType() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        IdSelector s = new IdSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        sb.broadcast(s2);
        assertThat(sb.one().get()).isSameAs(s2);
    }

    @Test
    public void relaysWhenMatched() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        QueryableBroadcaster qb = sb.chain(new QueryableBroadcaster());
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);

        assertThat(qb.find(ClassSelector.class).get()).isSameAs(s);
    }

    @Test
    public void relaysWhenNotMatched() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        QueryableBroadcaster qb = sb.chain(new QueryableBroadcaster());

        IdSelector s = new IdSelector("test");
        sb.broadcast(s);

        assertThat(qb.find(IdSelector.class).get()).isSameAs(s);
    }

    @Test
    public void resets() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);
        assertThat(sb.one().get()).isSameAs(s);

        sb.reset();

        ClassSelector s2 = new ClassSelector("test");
        sb.broadcast(s2);
        assertThat(sb.one().get()).isSameAs(s2);
    }
}
