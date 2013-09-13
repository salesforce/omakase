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

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * A {@link Broadcaster} that stores each event for later querying and retrieval.
 *
 * @author nmcwilliams
 */
public final class QueryableBroadcaster extends AbstractBroadcaster {
    /** important to maintain broadcast order */
    private final List<Syntax> collected = Lists.newArrayList();

    /**
     * Constructs a new {@link QueryableBroadcaster} instance that will <em>not</em> relay any events to another {@link
     * Broadcaster}.
     */
    public QueryableBroadcaster() {
        this(null);
    }

    /**
     * Constructs a new {@link QueryableBroadcaster} instance that will relay all events to the given {@link Broadcaster}.
     *
     * @param relay
     *     Wrap (decorate) this broadcaster. All broadcasts will be relayed to this one.
     */
    public QueryableBroadcaster(Broadcaster relay) {
        wrap(relay);
    }

    @Override
    public <T extends Syntax> void broadcast(T syntax) {
        collected.add(syntax);

        if (relay != null) {
            relay.broadcast(syntax);
        }
    }

    /**
     * Retrieves all broadcasted events.
     *
     * @return All broadcasted events.
     */
    public List<Syntax> all() {
        return ImmutableList.copyOf(collected);
    }

    /**
     * Gets all broadcasted events that are instances of the given class.
     *
     * @param <T>
     *     Type of the {@link Syntax} unit.
     * @param klass
     *     Filter {@link Syntax} units that are instances of this class.
     *
     * @return All matching {@link Syntax} units that are instances of the given class.
     */
    public <T extends Syntax> Iterable<T> filter(Class<T> klass) {
        return Iterables.filter(collected, klass);
    }

    /**
     * Finds the first {@link Syntax} unit that is an instance of the given class.
     *
     * @param <T>
     *     Type of the {@link Syntax} unit.
     * @param klass
     *     Get the first {@link Syntax} unit that is an instance of this class.
     *
     * @return The first matching {@link Syntax} unit that is an instance of the given class, or {@link Optional#absent()} if not
     *         present.
     */
    @SuppressWarnings("unchecked")
    public <T extends Syntax> Optional<T> find(Class<T> klass) {
        // Predicates.instanceOf ensures that this is a safe cast
        return (Optional<T>)Iterables.tryFind(collected, Predicates.instanceOf(klass));
    }

    /**
     * Similar to {@link #find(Class)}, except that this verifies at most one broadcasted event to have occurred.
     *
     * @param <T>
     *     Type of the {@link Syntax} unit.
     * @param klass
     *     Get the one and only {@link Syntax} unit that is an instance of this class.
     *
     * @return The single matching {@link Syntax} unit that is an instance of the given class, or {@link Optional#absent()} if not
     *         present.
     */
    public <T extends Syntax> Optional<T> findOnly(Class<T> klass) {
        Optional<T> found = find(klass);
        if (found.isPresent()) {
            checkState(collected.size() == 1, "expected to find only one broadcasted event");
        }
        return found;
    }
}
