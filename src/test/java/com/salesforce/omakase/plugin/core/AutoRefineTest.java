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

package com.salesforce.omakase.plugin.core;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.core.AutoRefine.Match;
import com.salesforce.omakase.plugin.syntax.DeclarationPlugin;
import com.salesforce.omakase.plugin.syntax.MediaPlugin;
import com.salesforce.omakase.plugin.syntax.SelectorPlugin;
import com.salesforce.omakase.plugin.syntax.UrlPlugin;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.fest.assertions.api.Assertions.assertThat;

/** Tests for {@link AutoRefine}. */
@SuppressWarnings("JavaDoc")
public class AutoRefineTest {
    private Registry registry;

    @Before
    public void setup() {
        this.registry = new Registry();
    }

    @Test
    public void atRulesOnly() {
        AutoRefine plugin = AutoRefine.only(Match.AT_RULES);
        plugin.dependencies(registry);

        assertThat(registry.registered).contains(MediaPlugin.class);
        assertThat(registry.registered).doesNotContain(SelectorPlugin.class);
    }

    @Test
    public void selectorsOnly() {
        AutoRefine plugin = AutoRefine.only(Match.SELECTORS);
        plugin.dependencies(registry);

        assertThat(registry.registered).containsOnly(SelectorPlugin.class);
    }

    @Test
    public void declarationsOnly() {
        AutoRefine plugin = AutoRefine.only(Match.DECLARATIONS);
        plugin.dependencies(registry);

        assertThat(registry.registered).containsOnly(DeclarationPlugin.class);
    }

    @Test
    public void functionsOnly() {
        AutoRefine plugin = AutoRefine.only(Match.FUNCTIONS);
        plugin.dependencies(registry);

        assertThat(registry.registered).contains(DeclarationPlugin.class);
        assertThat(registry.registered).contains(UrlPlugin.class);
        assertThat(registry.registered).doesNotContain(SelectorPlugin.class);
    }

    @Test
    public void rulesOnly() {
        AutoRefine plugin = AutoRefine.only(Match.RULES);
        plugin.dependencies(registry);

        assertThat(registry.registered).contains(DeclarationPlugin.class);
        assertThat(registry.registered).contains(UrlPlugin.class);
        assertThat(registry.registered).contains(SelectorPlugin.class);
        assertThat(registry.registered).doesNotContain(MediaPlugin.class);
    }

    @Test
    public void selectorsAndDeclarationsAndFunctions() {
        AutoRefine plugin = AutoRefine.only(Match.SELECTORS, Match.DECLARATIONS, Match.FUNCTIONS);
        plugin.dependencies(registry);

        assertThat(registry.registered).contains(DeclarationPlugin.class);
        assertThat(registry.registered).contains(UrlPlugin.class);
        assertThat(registry.registered).contains(SelectorPlugin.class);
        assertThat(registry.registered).doesNotContain(MediaPlugin.class);
    }

    @Test
    public void all() {
        AutoRefine plugin = new AutoRefine();
        plugin.dependencies(registry);

        assertThat(registry.registered).contains(DeclarationPlugin.class);
        assertThat(registry.registered).contains(UrlPlugin.class);
        assertThat(registry.registered).contains(SelectorPlugin.class);
        assertThat(registry.registered).contains(MediaPlugin.class);
    }

    @Test
    public void functionalTestAll() {
        AutoRefine plugin = AutoRefine.everything();
        QueryableBroadcaster queryable = new QueryableBroadcaster();

        String source = "@media all { .test{color:red} } .test2{background: url(foo.png)}";
        Omakase.source(source).use(plugin).broadcaster(queryable).process();

        assertThat(queryable.find(AtRule.class).get().isRefined()).isTrue();
        assertThat(queryable.find(Selector.class).get().isRefined()).isTrue();
        assertThat(queryable.find(Declaration.class).get().isRefined()).isTrue();
        assertThat(queryable.find(UrlFunctionValue.class).isPresent()).isTrue();
    }

    @Test
    public void functionalTestSome() {
        AutoRefine plugin = AutoRefine.only(Match.FUNCTIONS);
        QueryableBroadcaster queryable = new QueryableBroadcaster();

        String source = "@media all { .test{color:red} } .test2{background: url(foo.png)}";
        Omakase.source(source).use(plugin).broadcaster(queryable).process();

        assertThat(queryable.find(AtRule.class).get().isRefined()).isFalse();
        assertThat(queryable.find(Selector.class).get().isRefined()).isFalse();
        assertThat(queryable.find(Declaration.class).get().isRefined()).isTrue();
        assertThat(queryable.find(UrlFunctionValue.class).isPresent()).isTrue();
    }

    private static final class Registry implements PluginRegistry {
        public final List<Class<?>> registered = new ArrayList<>();

        @Override
        public <T extends Plugin> T require(Class<T> klass) {
            registered.add(klass);
            return null;
        }

        @Override
        public void register(Iterable<? extends Plugin> plugins) {}

        @Override
        public void register(Plugin plugin) {}

        @Override
        public <T extends Plugin> T require(Class<T> klass, Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends Plugin> Optional<T> retrieve(Class<T> klass) {
            return null;
        }
    }
}
