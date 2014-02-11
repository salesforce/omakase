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
        Omakase.source(".test{}").request(new RegistrationOrder5(list)).process();

        assertThat(list).containsExactly(
            RegistrationOrder2.class,
            RegistrationOrder1.class,
            RegistrationOrder3.class,
            RegistrationOrder4.class,
            RegistrationOrder5.class
        );
    }

    static class RegistrationOrderBase implements Plugin {
        List<Class<?>> list;

        RegistrationOrderBase(List<Class<?>> list) {
            this.list = list;
        }
    }

    public static final class RegistrationOrder1 extends RegistrationOrderBase {
        RegistrationOrder1(List<Class<?>> list) {
            super(list);
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

    public static final class RegistrationOrder2 extends RegistrationOrderBase {
        RegistrationOrder2(List<Class<?>> list) {
            super(list);
        }

        @Observe
        public void observe(Selector s) {
            list.add(this.getClass());
        }
    }

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
