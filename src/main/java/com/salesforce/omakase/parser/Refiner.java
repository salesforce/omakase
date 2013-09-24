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

package com.salesforce.omakase.parser;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * TODO description
 *
 * @author nmcwilliams
 */
public final class Refiner {
    private static final RefinableStrategy STANDARD = new StandardRefinableStrategy();

    private final Broadcaster broadcaster;
    private final ImmutableList<RefinableStrategy> strategies;

    public Refiner(Broadcaster broadcaster) {
        this(broadcaster, ImmutableList.<RefinableStrategy>of());
    }

    public Refiner(Broadcaster broadcaster, Iterable<RefinableStrategy> strategies) {
        this.broadcaster = broadcaster;
        this.strategies = ImmutableList.copyOf(strategies);
    }

    public void refine(AtRule atRule) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineAtRule(atRule, broadcaster)) return;
        }

        // fallback to the default refiner
        STANDARD.refineAtRule(atRule, broadcaster);
    }

    public void refine(Selector selector) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineSelector(selector, broadcaster)) return;
        }

        // fallback to the default refiner
        STANDARD.refineSelector(selector, broadcaster);
    }

    public void refine(Declaration declaration) {
        for (RefinableStrategy strategy : strategies) {
            if (strategy.refineDeclaration(declaration, broadcaster)) return;
        }

        // fallback to the default refiner
        STANDARD.refineDeclaration(declaration, broadcaster);
    }

    public Broadcaster broadcaster() {
        return broadcaster;
    }

    public void broadcast(Broadcastable broadcastable) {
        if (broadcastable.status() == Status.UNBROADCASTED) {
            broadcaster.broadcast(broadcastable);
        }
    }
}

