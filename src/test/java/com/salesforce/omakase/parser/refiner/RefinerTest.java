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
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

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
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy));
        refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomAtRule() {
        AtRuleStrategyFalse strategy1 = new AtRuleStrategyFalse();
        AtRuleStrategy strategy2 = new AtRuleStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy1, strategy2));
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
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy));
        refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomSelector() {
        SelectorStrategyFalse strategy1 = new SelectorStrategyFalse();
        SelectorStrategy strategy2 = new SelectorStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy1, strategy2));
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
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy));
        refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomDeclaration() {
        DeclarationStrategyFalse strategy1 = new DeclarationStrategyFalse();
        DeclarationStrategy strategy2 = new DeclarationStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy1, strategy2));
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

    @Test
    public void functionValueRefinement() {
        FunctionStrategy strategy = new FunctionStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy));
        refiner.refine(new RawFunction(1, 1, "test", "blah"));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleFunctionValue() {
        FunctionStrategyFalse strategy1 = new FunctionStrategyFalse();
        FunctionStrategy strategy2 = new FunctionStrategy();
        Refiner refiner = new Refiner(new QueryableBroadcaster(), ImmutableList.<RefinerStrategy>of(strategy1, strategy2));
        refiner.refine(new RawFunction(1, 1, "test", "blah"));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardFunctionValueRefinement() {
        Refiner refiner = new Refiner(new QueryableBroadcaster());
        refiner.refine(new RawFunction(1, 1, "test", "blah")); // no errors
    }

    public static final class AtRuleStrategy implements AtRuleRefiner {
        boolean called;

        @Override
        public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return true;
        }
    }

    public static final class AtRuleStrategyFalse implements AtRuleRefiner {
        boolean called;

        @Override
        public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }

    public static final class SelectorStrategy implements SelectorRefiner {
        boolean called;

        @Override
        public boolean refine(Selector selector, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return true;
        }
    }

    public static final class SelectorStrategyFalse implements SelectorRefiner {
        boolean called;

        @Override
        public boolean refine(Selector selector, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }

    public static final class DeclarationStrategy implements DeclarationRefiner {
        boolean called;

        @Override
        public boolean refine(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return true;
        }
    }

    public static final class DeclarationStrategyFalse implements DeclarationRefiner {
        boolean called;

        @Override
        public boolean refine(Declaration declaration, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }

    public static final class FunctionStrategy implements FunctionRefiner {
        boolean called;

        @Override
        public boolean refine(RawFunction raw, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return true;
        }
    }

    public static final class FunctionStrategyFalse implements FunctionRefiner {
        boolean called;

        @Override
        public boolean refine(RawFunction raw, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }
}
