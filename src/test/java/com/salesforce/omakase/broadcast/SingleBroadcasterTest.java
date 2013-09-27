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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link SingleBroadcaster}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class SingleBroadcasterTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void broadcastedExpectedUnit() {
        SingleBroadcaster<ClassSelector> sb = new SingleBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        sb.broadcast(s);
        assertThat(sb.broadcasted().get()).isSameAs(s);
    }

    @Test
    public void errorsIfMoreThanOneBroadcast() {
        SingleBroadcaster<ClassSelector> sb = new SingleBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Message.ONE_BROADCASTED_EVENT.message());
        sb.broadcast(s2);
    }

    @Test
    public void errorsIfBroadcastedIsWrongType() {
        SingleBroadcaster<ClassSelector> sb = new SingleBroadcaster<>(ClassSelector.class);
        IdSelector s = new IdSelector("test");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Expected to find an instance");
        sb.broadcast(s);
    }

    @Test
    public void testReset() {
        SingleBroadcaster<ClassSelector> sb = new SingleBroadcaster<>(ClassSelector.class);
        ClassSelector s = new ClassSelector("test");
        ClassSelector s2 = new ClassSelector("test");

        sb.broadcast(s);
        assertThat(sb.broadcasted().get()).isSameAs(s);

        sb.reset().broadcast(s2);
        assertThat(sb.broadcasted().get()).isSameAs(s2);
    }
}
