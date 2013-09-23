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

import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link AutoRefiner}. */
@SuppressWarnings("JavaDoc")
public class AutoRefinerTest {
    private AtRule atRule;
    private Selector selector;
    private Declaration declaration;
    private AutoRefiner autoRefiner;

    @Before
    public void setup() {
        Broadcaster broadcaster = new QueryableBroadcaster();
        atRule = new AtRule(5, 5, "media", new RawSyntax(1, 1, "all"), new RawSyntax(1, 1, "{p {color:red}}"), broadcaster);
        selector = new Selector(new RawSyntax(1, 1, ".class"), broadcaster);
        declaration = new Declaration(new RawSyntax(1, 1, "color"), new RawSyntax(1, 1, "red"), broadcaster);
        autoRefiner = new AutoRefiner();
    }

    @Test
    public void selectorsOnly() {
        autoRefiner.selectors();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isFalse();
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isFalse();
    }

    @Test
    public void declarationsOnly() {
        autoRefiner.declarations();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        assertThat(atRule.isRefined()).isFalse();
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isTrue();
    }

    @Test

    public void atRulesOnly() {
        autoRefiner.atRules();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        // assertThat(atRule.isRefined()).isTrue(); TODO once at-rules are refined
        assertThat(selector.isRefined()).isFalse();
        assertThat(declaration.isRefined()).isFalse();
    }

    @Test
    public void all() {
        autoRefiner.all();
        autoRefiner.refine(atRule);
        autoRefiner.refine(selector);
        autoRefiner.refine(declaration);

        // assertThat(atRule.isRefined()).isTrue(); TODO once at-rules are refined
        assertThat(selector.isRefined()).isTrue();
        assertThat(declaration.isRefined()).isTrue();
    }
}
