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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link MasterRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class MasterRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void customAtRuleRefinement() {
        AtRuleStrategyFull strategy = new AtRuleStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy);
        refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomAtRule() {
        AtRuleStrategyNone strategy1 = new AtRuleStrategyNone();
        AtRuleStrategyFull strategy2 = new AtRuleStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy1).register(strategy2);
        refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardAtRuleRefinement() {
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster());
        AtRule ar = new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner);
        refiner.refine(ar); // no errors
    }

    @Test
    public void customSelectorRefinement() {
        SelectorStrategyFull strategy = new SelectorStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy);
        refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomSelector() {
        SelectorStrategyNone strategy1 = new SelectorStrategyNone();
        SelectorStrategyFull strategy2 = new SelectorStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy1).register(strategy2);
        refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardSelectorRefinement() {
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster());
        Selector selector = new Selector(new RawSyntax(1, 1, "p"), refiner);
        refiner.refine(selector);
        assertThat(selector.isRefined()).isTrue();
    }

    @Test
    public void customDeclarationRefinement() {
        DeclarationStrategyFull strategy = new DeclarationStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy);
        refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleCustomDeclaration() {
        DeclarationStrategyNone strategy1 = new DeclarationStrategyNone();
        DeclarationStrategyFull strategy2 = new DeclarationStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy1).register(strategy2);
        refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardDeclarationRefinement() {
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster());
        Declaration declaration = new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner);
        refiner.refine(declaration);
    }

    @Test
    public void functionValueRefinement() {
        FunctionStrategyFull strategy = new FunctionStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy);
        refiner.refine(new RawFunction(1, 1, "test", "blah"));
        assertThat(strategy.called).isTrue();
    }

    @Test
    public void testMultipleFunctionValue() {
        FunctionStrategyFalse strategy1 = new FunctionStrategyFalse();
        FunctionStrategyFull strategy2 = new FunctionStrategyFull();
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy1).register(strategy2);
        refiner.refine(new RawFunction(1, 1, "test", "blah"));
        assertThat(strategy1.called).isTrue();
        assertThat(strategy2.called).isTrue();
    }

    @Test
    public void standardFunctionValueRefinement() {
        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster());
        refiner.refine(new RawFunction(1, 1, "test", "blah")); // no errors
    }

    @Test
    public void atRuleAllNone() {
        AtRuleStrategyNone strategy1 = new AtRuleStrategyNone();
        AtRuleStrategyNone strategy2 = new AtRuleStrategyNone();
        AtRuleStrategyNone strategy3 = new AtRuleStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));

        assertThat(result).isSameAs(Refinement.NONE);
    }

    @Test
    public void atRuleAnyFull() {
        AtRuleStrategyFull strategy1 = new AtRuleStrategyFull();
        AtRuleStrategyNone strategy2 = new AtRuleStrategyNone();
        AtRuleStrategyNone strategy3 = new AtRuleStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void atRulePartialThenFull() {
        AtRuleStrategyPartial strategy1 = new AtRuleStrategyPartial();
        AtRuleStrategyNone strategy2 = new AtRuleStrategyNone();
        AtRuleStrategyFull strategy3 = new AtRuleStrategyFull();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void atRuleIfPartialThenNone() {
        AtRuleStrategyPartial strategy1 = new AtRuleStrategyPartial();
        AtRuleStrategyNone strategy2 = new AtRuleStrategyNone();
        AtRuleStrategyNone strategy3 = new AtRuleStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));

        assertThat(result).isSameAs(Refinement.PARTIAL);
    }

    @Test
    public void atRuleAllPartial() {
        AtRuleStrategyPartial strategy1 = new AtRuleStrategyPartial();
        AtRuleStrategyPartial strategy2 = new AtRuleStrategyPartial();
        AtRuleStrategyPartial strategy3 = new AtRuleStrategyPartial();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new AtRule(5, 5, "t", new RawSyntax(1, 1, "t"), new RawSyntax(1, 1, "t"), refiner));

        assertThat(result).isSameAs(Refinement.PARTIAL);
    }

    @Test
    public void selectorAllNone() {
        SelectorStrategyNone strategy1 = new SelectorStrategyNone();
        SelectorStrategyNone strategy2 = new SelectorStrategyNone();
        SelectorStrategyNone strategy3 = new SelectorStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); //standard takes it
    }

    @Test
    public void selectorAnyFull() {
        SelectorStrategyFull strategy1 = new SelectorStrategyFull();
        SelectorStrategyNone strategy2 = new SelectorStrategyNone();
        SelectorStrategyNone strategy3 = new SelectorStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void selectorPartialThenFull() {
        SelectorStrategyPartial strategy1 = new SelectorStrategyPartial();
        SelectorStrategyNone strategy2 = new SelectorStrategyNone();
        SelectorStrategyFull strategy3 = new SelectorStrategyFull();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void selectorIfPartialThenNone() {
        SelectorStrategyPartial strategy1 = new SelectorStrategyPartial();
        SelectorStrategyNone strategy2 = new SelectorStrategyNone();
        SelectorStrategyNone strategy3 = new SelectorStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); // standard takes it
    }

    @Test
    public void selectorAllPartial() {
        SelectorStrategyPartial strategy1 = new SelectorStrategyPartial();
        SelectorStrategyPartial strategy2 = new SelectorStrategyPartial();
        SelectorStrategyPartial strategy3 = new SelectorStrategyPartial();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Selector(new RawSyntax(1, 1, "p"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); // standard takes it
    }

    @Test
    public void declarationAllNone() {
        DeclarationStrategyNone strategy1 = new DeclarationStrategyNone();
        DeclarationStrategyNone strategy2 = new DeclarationStrategyNone();
        DeclarationStrategyNone strategy3 = new DeclarationStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); //standard takes it
    }

    @Test
    public void declarationAnyFull() {
        DeclarationStrategyFull strategy1 = new DeclarationStrategyFull();
        DeclarationStrategyNone strategy2 = new DeclarationStrategyNone();
        DeclarationStrategyNone strategy3 = new DeclarationStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void declarationPartialThenFull() {
        DeclarationStrategyPartial strategy1 = new DeclarationStrategyPartial();
        DeclarationStrategyNone strategy2 = new DeclarationStrategyNone();
        DeclarationStrategyFull strategy3 = new DeclarationStrategyFull();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));

        assertThat(result).isSameAs(Refinement.FULL);
    }

    @Test
    public void declarationIfPartialThenNone() {
        DeclarationStrategyPartial strategy1 = new DeclarationStrategyPartial();
        DeclarationStrategyNone strategy2 = new DeclarationStrategyNone();
        DeclarationStrategyNone strategy3 = new DeclarationStrategyNone();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); // standard takes it
    }

    @Test
    public void declarationAllPartial() {
        DeclarationStrategyPartial strategy1 = new DeclarationStrategyPartial();
        DeclarationStrategyPartial strategy2 = new DeclarationStrategyPartial();
        DeclarationStrategyPartial strategy3 = new DeclarationStrategyPartial();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster())
            .register(strategy1)
            .register(strategy2)
            .register(strategy3);

        Refinement result = refiner.refine(new Declaration(new RawSyntax(5, 5, "color"), new RawSyntax(5, 5, "red"), refiner));

        assertThat(result).isSameAs(Refinement.FULL); // standard takes it
    }

    @Test
    public void rawFunctionPartialThrowsError() {
        FunctionStrategyPartial strategy1 = new FunctionStrategyPartial();

        MasterRefiner refiner = new MasterRefiner(new QueryableBroadcaster()).register(strategy1);

        exception.expect(UnsupportedOperationException.class);
        refiner.refine(new RawFunction(1, 1, "test", "blah"));
    }

    public static final class AtRuleStrategyFull implements AtRuleRefiner {
        boolean called;

        @Override
        public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.FULL;
        }
    }

    public static final class AtRuleStrategyPartial implements AtRuleRefiner {
        boolean called;

        @Override
        public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.PARTIAL;
        }
    }

    public static final class AtRuleStrategyNone implements AtRuleRefiner {
        boolean called;

        @Override
        public Refinement refine(AtRule atRule, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.NONE;
        }
    }

    public static final class SelectorStrategyFull implements SelectorRefiner {
        boolean called;

        @Override
        public Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.FULL;
        }
    }

    public static final class SelectorStrategyPartial implements SelectorRefiner {
        boolean called;

        @Override
        public Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.PARTIAL;
        }
    }

    public static final class SelectorStrategyNone implements SelectorRefiner {
        boolean called;

        @Override
        public Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.NONE;
        }
    }

    public static final class DeclarationStrategyFull implements DeclarationRefiner {
        boolean called;

        @Override
        public Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            broadcaster.broadcast(new PropertyValue());
            return Refinement.FULL;
        }
    }

    public static final class DeclarationStrategyPartial implements DeclarationRefiner {
        boolean called;

        @Override
        public Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            broadcaster.broadcast(new PropertyValue());
            return Refinement.PARTIAL;
        }
    }

    public static final class DeclarationStrategyNone implements DeclarationRefiner {
        boolean called;

        @Override
        public Refinement refine(Declaration declaration, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.NONE;
        }
    }

    public static final class FunctionStrategyFull implements FunctionRefiner {
        boolean called;

        @Override
        public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.FULL;
        }
    }

    public static final class FunctionStrategyPartial implements FunctionRefiner {
        boolean called;

        @Override
        public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.PARTIAL;
        }
    }

    public static final class FunctionStrategyFalse implements FunctionRefiner {
        boolean called;

        @Override
        public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
            called = true;
            return Refinement.NONE;
        }
    }
}
