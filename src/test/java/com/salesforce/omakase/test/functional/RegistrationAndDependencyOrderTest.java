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

package com.salesforce.omakase.test.functional;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.Plugin;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Cross-functional test focusing on correct plugin registration dependency order.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class RegistrationAndDependencyOrderTest {
    @Test
    public void registrationOrder() {
        List<Class<?>> list = Lists.newArrayList();
        Omakase.source(".test{}").use(new RegistrationOrder5(list)).process();

        assertThat(list).containsExactly(
            RegistrationOrder2.class,
            RegistrationOrder1.class,
            RegistrationOrder3.class,
            RegistrationOrder4.class,
            RegistrationOrder5.class
        );
    }

    static class RegistrationOrderBase implements Plugin {
        final List<Class<?>> list;

        RegistrationOrderBase(List<Class<?>> list) {
            this.list = list;
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static final class RegistrationOrder1 extends RegistrationOrderBase {
        RegistrationOrder1(List<Class<?>> list) {
            super(list);
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static final class RegistrationOrder2 extends RegistrationOrderBase {
        RegistrationOrder2(List<Class<?>> list) {
            super(list);
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static final class RegistrationOrder3 extends RegistrationOrderBase implements DependentPlugin {
        RegistrationOrder3(List<Class<?>> list) {
            super(list);
        }

        @Override
        public void dependencies(PluginRegistry registry) {
            registry.require(RegistrationOrder1.class, new Supplier<RegistrationOrder1>() {
                @Override
                public RegistrationOrder1 get() {
                    return new RegistrationOrder1(list);
                }
            });
            registry.require(RegistrationOrder2.class, new Supplier<RegistrationOrder2>() {
                @Override
                public RegistrationOrder2 get() {
                    return new RegistrationOrder2(list);
                }
            });
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static final class RegistrationOrder4 extends RegistrationOrderBase implements DependentPlugin {
        RegistrationOrder4(List<Class<?>> list) {
            super(list);
        }

        @Override
        public void dependencies(PluginRegistry registry) {
            registry.require(RegistrationOrder3.class, new Supplier<RegistrationOrder3>() {
                @Override
                public RegistrationOrder3 get() {
                    return new RegistrationOrder3(list);
                }
            });
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static final class RegistrationOrder5 extends RegistrationOrderBase implements DependentPlugin {
        RegistrationOrder5(List<Class<?>> list) {
            super(list);
        }

        @Override
        public void dependencies(PluginRegistry registry) {
            registry.require(RegistrationOrder2.class, new Supplier<RegistrationOrder2>() {
                @Override
                public RegistrationOrder2 get() {
                    return new RegistrationOrder2(list);
                }
            });
            registry.require(RegistrationOrder4.class, new Supplier<RegistrationOrder4>() {
                @Override
                public RegistrationOrder4 get() {
                    return new RegistrationOrder4(list);
                }
            });
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }
}
