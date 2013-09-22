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

package com.salesforce.omakase;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.Observe;
import com.salesforce.omakase.emitter.PreProcess;
import com.salesforce.omakase.emitter.Rework;
import com.salesforce.omakase.emitter.Validate;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.plugin.BroadcastingPlugin;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.plugin.PostProcessingPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("JavaDoc")
public class ContextTest {
    @Rule public ExpectedException exception = ExpectedException.none();

    Context c;

    @Before
    public void setup() {
        c = new Context();
    }

    @Test
    public void register() {
        TestPlugin plugin = new TestPlugin();
        c.register(plugin);
        assertThat(c.retrieve(TestPlugin.class).get()).isSameAs(plugin);
    }

    @Test
    public void errorIfRegisterSameType() {
        TestPlugin plugin = new TestPlugin();
        c.register(plugin);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Only one plugin instance");
        c.register(new TestPlugin());
    }

    @Test
    public void requireLibraryPlugin() {
        SyntaxTree tree = c.require(SyntaxTree.class);
        assertThat(c.retrieve(SyntaxTree.class).get()).isSameAs(tree);
    }

    @Test
    public void requireCustomPluginInvalid() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No supplier defined");
        c.require(TestPlugin.class);
    }

    @Test
    public void requireCustomPluginValid() {
        final TestPlugin tp = new TestPlugin();

        c.require(TestPlugin.class, new Supplier<TestPlugin>() {
            @Override
            public TestPlugin get() {
                return tp;
            }
        });

        assertThat(c.retrieve(TestPlugin.class).get()).isSameAs(tp);
    }

    @Test
    public void retrieveNotPresent() {
        assertThat(c.retrieve(TestPlugin.class).isPresent()).isFalse();
    }

    @Test
    public void broadcast() {
        QueryableBroadcaster broadcaster = new QueryableBroadcaster();
        ClassSelector cs = new ClassSelector("class");
        c.broadcaster(broadcaster);
        c.broadcast(cs);
        assertThat(broadcaster.all()).hasSize(1);
    }

    @Test
    public void beforeMethodSetsErrorManager() {
        TestErrorManager em = new TestErrorManager();
        c.register(new FailingPlugin());
        c.broadcast(new ClassSelector("class"));
        c.before(em);
        c.after();
        assertThat(em.reported).isTrue();
    }

    @Test
    public void beforeMethodInvokesDependencies() {
        TestDependentPlugin plugin = new TestDependentPlugin();
        c.register(plugin);
        c.before(new TestErrorManager());
        assertThat(plugin.dependenciesCalled).isTrue();
    }

    @Test
    public void dependenciesThatLeadToMoreDependencies() {
        c.register(new TestDependentPlugin2());
        c.before(new TestErrorManager());
        assertThat(c.retrieve(TestDependentPlugin2.class).isPresent()).isTrue();
        assertThat(c.retrieve(TestDependentPlugin.class).isPresent()).isTrue();
        assertThat(c.retrieve(TestDependentPlugin.class).get().dependenciesCalled).isTrue();
    }

    @Test
    public void beforeMethodSendsBroadcaster() {
        TestBroadcastingPlugin tbp = new TestBroadcastingPlugin();
        c.register(tbp);
        c.before(new TestErrorManager());
        assertThat(tbp.broadcasterCalled).isTrue();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void afterMethodPhaseOrder() {
        PluginWithPreProcess preprocess = new PluginWithPreProcess();
        PluginWithObserve observe = new PluginWithObserve();
        PluginWithRework rework = new PluginWithRework();
        PluginWithValidate validate = new PluginWithValidate();

        c.register(Lists.newArrayList(rework, preprocess, validate, observe));

        c.before(new TestErrorManager());
        c.broadcast(new ClassSelector("test"));
        c.after();

        assertThat(preprocess.order < observe.order).isTrue();
        assertThat(preprocess.order < rework.order).isTrue();
        assertThat(preprocess.order < validate.order).isTrue();

        assertThat(observe.order < validate.order).isTrue();
        assertThat(observe.order > preprocess.order).isTrue();

        assertThat(rework.order < validate.order).isTrue();
        assertThat(rework.order > preprocess.order).isTrue();

        assertThat(validate.order > rework.order).isTrue();
        assertThat(validate.order > observe.order).isTrue();
        assertThat(validate.order > preprocess.order).isTrue();
    }

    @Test
    public void afterMethodNotifyPostProcessor() {
        TestPostProcessingPlugin tpp = new TestPostProcessingPlugin();
        c.register(tpp);
        c.before(new TestErrorManager());
        c.after();
        assertThat(tpp.postProcessCalled).isTrue();
    }

    public static final class TestPlugin implements Plugin {}

    public static final class FailingPlugin implements Plugin {
        @Validate
        public void classSelector(ClassSelector cs, ErrorManager em) {
            em.report(ErrorLevel.FATAL, cs, "test");
        }
    }

    public static final class TestDependentPlugin implements DependentPlugin {
        boolean dependenciesCalled;

        @Override
        public void dependencies(PluginRegistry registry) {
            dependenciesCalled = true;
        }
    }

    public static final class TestDependentPlugin2 implements DependentPlugin {
        @Override
        public void dependencies(PluginRegistry registry) {
            registry.require(TestDependentPlugin.class, new Supplier<TestDependentPlugin>() {
                @Override
                public TestDependentPlugin get() {
                    return new TestDependentPlugin();
                }
            });
        }
    }

    public static final class TestPostProcessingPlugin implements PostProcessingPlugin {
        boolean postProcessCalled;

        @Override
        public void postProcess(PluginRegistry registry) {
            postProcessCalled = true;
        }
    }

    public static final class TestBroadcastingPlugin implements BroadcastingPlugin {
        boolean broadcasterCalled;

        @Override
        public void broadcaster(Broadcaster broadcaster) {
            broadcasterCalled = true;
        }
    }

    public static final class TestErrorManager implements ErrorManager {
        boolean reported;

        @Override
        public void report(ErrorLevel level, ParserException exception) {
            reported = true;
        }

        @Override
        public void report(ErrorLevel level, Syntax cause, String message) {
            reported = true;
        }
    }

    @SuppressWarnings("StaticNonFinalField") static int num;

    public static final class PluginWithPreProcess implements Plugin {
        int order;

        @PreProcess
        @SuppressWarnings("UnusedParameters")
        public void classSelector(ClassSelector cs) {
            order = num++;
        }
    }

    public static final class PluginWithObserve implements Plugin {
        int order;

        @Observe
        @SuppressWarnings("UnusedParameters")
        public void classSelector(ClassSelector cs) {
            order = num++;
        }
    }

    public static final class PluginWithRework implements Plugin {
        int order;

        @Rework
        @SuppressWarnings("UnusedParameters")
        public void classSelector(ClassSelector cs) {
            order = num++;
        }
    }

    public static final class PluginWithValidate implements Plugin {
        int order;

        @Validate
        @SuppressWarnings("UnusedParameters")
        public void classSelector(ClassSelector cs, ErrorManager em) {
            order = num++;
        }
    }
}
