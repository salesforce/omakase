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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.Sets;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.AbstractBroadcaster;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link StandardRefinableStrategy}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class StandardRefinableStrategyTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void refineSelector() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id");
        Selector selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(selector.refine().parts()).isNotEmpty();
    }

    @Test
    public void refineSelectorThrowsErrorIfHasUnparsableContent() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id !!!!");
        Selector selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNPARSABLE_SELECTOR.message());
        selector.refine();
    }

    @Test
    public void refinedSelectorAddsOrphanedComments() {
        RawSyntax raw = new RawSyntax(5, 2, ".class > #id /*orphaned*/");
        Selector selector = new Selector(raw, new Refiner(new StatusChangingBroadcaster()));
        assertThat(selector.refine().orphanedComments()).isNotEmpty();
    }

    @Test
    public void refineDeclaration() {
        RawSyntax rawName = new RawSyntax(2, 3, "display");
        RawSyntax rawValue = new RawSyntax(2, 5, "none");
        Declaration declaration = new Declaration(rawName, rawValue, new Refiner(new StatusChangingBroadcaster()));
        assertThat(declaration.refine().propertyName()).isNotNull();
        assertThat(declaration.refine().propertyValue()).isNotNull();
    }

    @Test
    public void refineThrowsErrorIfUnparsableContent() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none ^^^^^^");

        exception.expect(ParserException.class);
        exception.expectMessage(Message.UNPARSABLE_VALUE.message());
        new Declaration(name, value, new Refiner(new StatusChangingBroadcaster())).refine();
    }

    @Test
    public void refineAddsOrphanedComments() {
        RawSyntax name = new RawSyntax(2, 3, "display");
        RawSyntax value = new RawSyntax(2, 5, "none /*orphaned*/");
        Declaration d = new Declaration(name, value, new Refiner(new StatusChangingBroadcaster())).refine();
        assertThat(d.orphanedComments()).isNotEmpty();
    }

    private static final class StatusChangingBroadcaster extends AbstractBroadcaster {
        private final Set<Broadcastable> all = Sets.newHashSet();

        @Override
        public void broadcast(Broadcastable broadcastable) {
            if (all.contains(broadcastable)) {
                fail("unit shouldn't be broadcasted twice!");
            }
            all.add(broadcastable);
            broadcastable.status(Status.BROADCASTED_PREPROCESS);
        }
    }
}
