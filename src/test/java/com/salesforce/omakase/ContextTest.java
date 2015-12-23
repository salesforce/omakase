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

package com.salesforce.omakase;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorLevel;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.token.BaseTokenFactory;
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
    @Rule public final ExpectedException exception = ExpectedException.none();

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
    public void noErrorIfRequireSameTokenFactory() {
        CustomTokenFactory custom = new CustomTokenFactory();
        CustomTokenFactory ret = c.requireTokenFactory(CustomTokenFactory.class, com.google.common.base.Suppliers.ofInstance(custom));
        assertThat(ret).isSameAs(custom);

        // since already registered, should get the previous instance
        ret = c.requireTokenFactory(CustomTokenFactory.class, com.google.common.base.Suppliers.ofInstance(new CustomTokenFactory()));
        assertThat(ret).isSameAs(custom);
    }

    @Test
    public void errorsIfRequireMultipleTokenFactories() {
        c.requireTokenFactory(CustomTokenFactory.class, com.google.common.base.Suppliers.ofInstance(new CustomTokenFactory()));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Only one token factory is allowed");
        c.requireTokenFactory(CustomTokenFactory2.class, com.google.common.base.Suppliers.ofInstance(new CustomTokenFactory2()));
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
    public void errorManager() {
        TestErrorManager em = new TestErrorManager();
        c.register(new FailingPlugin());
        c.broadcast(new ClassSelector("class"));
        c.errorManager(em);
        c.before();
        c.after();
        assertThat(em.reported).isTrue();
    }

    @Test
    public void beforeMethodInvokesDependencies() {
        TestDependentPlugin plugin = new TestDependentPlugin();
        c.register(plugin);
        assertThat(plugin.dependenciesCalled).isTrue();
    }

    @Test
    public void dependenciesThatLeadToMoreDependencies() {
        c.register(new TestDependentPlugin2());
        assertThat(c.retrieve(TestDependentPlugin2.class).isPresent()).isTrue();
        assertThat(c.retrieve(TestDependentPlugin.class).isPresent()).isTrue();
        assertThat(c.retrieve(TestDependentPlugin.class).get().dependenciesCalled).isTrue();
    }

    @Test
    public void beforeMethodSendsBroadcaster() {
        TestBroadcastingPlugin tbp = new TestBroadcastingPlugin();
        c.register(tbp);
        c.before();
        assertThat(tbp.broadcasterCalled).isTrue();
    }

    @Test
    public void afterMethodPhaseOrder() {
        PluginWithObserve observe = new PluginWithObserve();
        PluginWithRework rework = new PluginWithRework();
        PluginWithValidate validate = new PluginWithValidate();

        c.register(Lists.newArrayList(rework, validate, observe));

        c.before();
        c.broadcast(new ClassSelector("test"));
        c.after();

        assertThat(observe.order < validate.order).isTrue();
        assertThat(rework.order < validate.order).isTrue();
    }

    @Test
    public void afterMethodNotifyPostProcessor() {
        TestPostProcessingPlugin tpp = new TestPostProcessingPlugin();
        c.register(tpp);
        c.before();
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

        @Override
        public String getSourceName() {
            return null;
        }
    }

    @SuppressWarnings("StaticNonFinalField") static int num;

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

    public static final class CustomTokenFactory extends BaseTokenFactory {}

    public static final class CustomTokenFactory2 extends BaseTokenFactory {}
}
