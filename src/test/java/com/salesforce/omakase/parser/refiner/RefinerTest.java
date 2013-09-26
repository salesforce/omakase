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

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.RefinableStrategy;
import com.salesforce.omakase.parser.refiner.Refiner;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link Refiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RefinerTest {
    @Test
    public void customAtRuleRefinement() {
        AtRuleStrategy strategy = new AtRuleStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy));
        refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomAtRule() {
        AtRuleStrategyFalse strategy1 = new AtRuleStrategyFalse();
        AtRuleStrategy strategy2 = new AtRuleStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy1, strategy2));
        refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardAtRuleRefinement() {
        Refiner refiner = new Refiner(new QueryableBroadcaster());
        AtRule ar = new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner);
        refiner.refine(ar); // no errors
    }

    @Test
    public void customSelectorRefinement() {
        SelectorStrategy strategy = new SelectorStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy));
        refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomSelector() {
        SelectorStrategyFalse strategy1 = new SelectorStrategyFalse();
        SelectorStrategy strategy2 = new SelectorStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy1, strategy2));
        refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardSelectorRefinement() {
        Refiner refiner = new Refiner(new QueryableBroadcaster());
        Selector selector = new Selector(new RawSyntax(1, 1, "p"), refiner);
        refiner.refine(selector);
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void customDeclarationRefinement() {
        DeclarationStrategy strategy = new DeclarationStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy));
        refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomDeclaration() {
        DeclarationStrategyFalse strategy1 = new DeclarationStrategyFalse();
        DeclarationStrategy strategy2 = new DeclarationStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinableStrategy>of(strategy1, strategy2));
        refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardDeclarationRefinement() {
        Refiner refiner = new Refiner(new QueryableBroadcaster());
        Declaration declaration = new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner);
        refiner.refine(declaration);
    }

    public static final class AtRuleStrategy implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            called = true;
            return true;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            return false;
        }
    }

    public static final class AtRuleStrategyFalse implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            called = true;
            return false;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            return false;
        }
    }

    public static final class SelectorStrategy implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            called = true;
            return true;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            return false;
        }
    }

    public static final class SelectorStrategyFalse implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            called = true;
            return false;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            return false;
        }
    }

    public static final class DeclarationStrategy implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            called = true;
            return true;
        }
    }

    public static final class DeclarationStrategyFalse implements RefinableStrategy {
        boolean called;

        @Override
        public boolean refineAtRule(AtRule atRule, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineSelector(Selector selector, Broadcaster broadcaster) {
            return false;
        }

        @Override
        public boolean refineDeclaration(Declaration declaration, Broadcaster broadcaster) {
            called = true;
            return false;
        }
    }
}
