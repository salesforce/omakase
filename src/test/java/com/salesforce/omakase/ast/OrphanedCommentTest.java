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

package com.salesforce.omakase.ast;

import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link OrphanedComment}. */
@SuppressWarnings("JavaDoc")
public class OrphanedCommentTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private OrphanedComment o;

    @Before
    public void setup() {
        this.o = new OrphanedComment("test", OrphanedComment.Location.DECLARATION);
    }

    @Test
    public void defaultStatusIsUnbroadcasted() {
        assertThat(o.status()).isSameAs(Status.UNBROADCASTED);
    }

    @Test
    public void testStatus() throws Exception {
        o.status(Status.BROADCASTED_PREPROCESS);
        assertThat(o.status()).isSameAs(Status.BROADCASTED_PREPROCESS);
    }

    @Test
    public void testPropagateBroadcastUnbroadcasted() throws Exception {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        o.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).hasSize(1);
    }

    @Test
    public void testPropagateBroadcastAlreadyBroadcasted() throws Exception {
        o.status(Status.BROADCASTED_PREPROCESS);
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        o.propagateBroadcast(broadcaster);
        assertThat(broadcaster.all()).isEmpty();
    }
}
