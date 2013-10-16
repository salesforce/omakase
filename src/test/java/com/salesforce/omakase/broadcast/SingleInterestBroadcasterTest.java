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

import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SingleInterestBroadcaster}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SingleInterestBroadcasterTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void broadcastedExpectedUnit() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);
        assertThat(sb.broadcasted().get()).isSameAs(s);
    }

    @Test
    public void onlyStoresFirstOccurrence() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        sb.broadcast(s2);
        assertThat(sb.broadcasted().get()).isSameAs(s);
    }

    @Test
    public void ignoresBroadcastsOfWrongType() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        IdSelector s = new IdSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        sb.broadcast(s2);
        assertThat(sb.broadcasted().get()).isSameAs(s2);
    }

    @Test
    public void testReset() {
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        assertThat(sb.broadcasted().get()).isSameAs(s);

        sb.reset().broadcast(s2);
        assertThat(sb.broadcasted().get()).isSameAs(s2);
    }

    @Test
    public void relaysWhenMatched() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class, qb);
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);

        assertThat(qb.find(ClassSelector.class).get()).isSameAs(s);
    }

    @Test
    public void relaysWhenNotMatched() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        SingleInterestBroadcaster<ClassSelector> sb = new SingleInterestBroadcaster<>(ClassSelector.class, qb);
        IdSelector s = new IdSelector("test");
        sb.broadcast(s);

        assertThat(qb.find(IdSelector.class).get()).isSameAs(s);
    }
}
