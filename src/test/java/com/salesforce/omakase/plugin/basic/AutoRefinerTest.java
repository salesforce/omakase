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

package com.salesforce.omakase.plugin.basic;

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.refiner.AtRuleRefinerStrategy;
import com.salesforce.omakase.parser.refiner.FunctionValueRefinerStrategy;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.refiner.RefinerStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.*;

/** Unit tests for {@link AutoRefiner}. */
@SuppressWarnings("JavaDoc")
public class AutoRefinerTest {
    private AtRule atRule;
    private Selector selector;
    private Declaration declaration;
    private FunctionValue functionValue;
    private AutoRefiner autoRefiner;
    private CustomFunctionStrategy customFunctionStrategy;
    private CustomAtRuleStrategy customAtRuleStrategy;

    @Before
    public void setup() {
        customFunctionStrategy = new CustomFunctionStrategy();
        customAtRuleStrategy = new CustomAtRuleStrategy();
        Set<RefinerStrategy> set = Sets.newHashSet(customAtRuleStrategy, customFunctionStrategy);
        Refiner refiner = new Refiner(new QueryableBroadcaster(), set);

        atRule = new AtRule(5, 5, "media", new RawSyntax(1, 1, "all"), new RawSyntax(1, 1, "{p {color:red}}"), refiner);
        selector = new Selector(new RawSyntax(1, 1, ".class"), refiner);
        declaration = new Declaration(new RawSyntax(1, 1, "color"), new RawSyntax(1, 1, "red"), refiner);
        functionValue = new FunctionValue(1, 1, "test", "test", refiner);
        autoRefiner = new AutoRefiner();
    }

    @Test
    public void selectorsOnly() {
        autoRefiner.selectors();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);
        autoRefiner.refine(functionValue);

        assertThat(customAtRuleStrategy.called).isFalse();
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isFalse();
        assertThat(customFunctionStrategy.called).isFalse();
    }

    @Test
    public void declarationsOnly() {
        autoRefiner.declarations();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);
        autoRefiner.refine(functionValue);

        assertThat(customAtRuleStrategy.called).isFalse();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isTrue();
        assertThat(customFunctionStrategy.called).isFalse();
    }

    @Test

    public void atRulesOnly() {
        autoRefiner.atRules();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);
        autoRefiner.refine(functionValue);

        assertThat(customAtRuleStrategy.called).isTrue();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isFalse();
        assertThat(customFunctionStrategy.called).isFalse();
    }

    @Test
    public void functionsOnly() {
        autoRefiner.functions();

        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);
        autoRefiner.refine(functionValue);

        assertThat(customAtRuleStrategy.called).isFalse();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isFalse();
        assertThat(customFunctionStrategy.called).isTrue();
    }

    @Test
    public void all() {
        autoRefiner.all();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);
        autoRefiner.refine(functionValue);

        assertThat(customAtRuleStrategy.called).isTrue();
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isTrue();
        assertThat(customFunctionStrategy.called).isTrue();
    }

    public static final class CustomAtRuleStrategy implements AtRuleRefinerStrategy {
        boolean called;

        @Override
        public boolean refine(AtRule atRule, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }

    public static final class CustomFunctionStrategy implements FunctionValueRefinerStrategy {
        boolean called;

        @Override
        public boolean refine(FunctionValue functionValue, Broadcaster broadcaster, Refiner refiner) {
            called = true;
            return false;
        }
    }
}
