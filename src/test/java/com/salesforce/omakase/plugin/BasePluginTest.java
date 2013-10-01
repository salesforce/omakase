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

package com.salesforce.omakase.plugin;

import com.google.common.collect.Sets;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link BasePlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class BasePluginTest {
    @Test
    public void hasMethodForEverySubscribable() throws InvocationTargetException, IllegalAccessException {
        Method[] declaredMethods = BasePlugin.class.getDeclaredMethods();

        Set<Class<?>> subscriptions = Sets.newHashSetWithExpectedSize(32);
        for (Method method : declaredMethods) {
            subscriptions.add(method.getParameterTypes()[0]);
        }

        Reflections reflections = new Reflections("com.salesforce.omakase.ast");
        Set<Class<?>> subscribables = Sets.newHashSet(reflections.getTypesAnnotatedWith(Subscribable.class));

        assertThat(subscriptions).containsAll(subscribables);

        Sets.SetView<Class<?>> difference = Sets.difference(subscriptions, subscribables);
        assertThat(difference).describedAs("BasePlugin has a subscription to an object that is not subscribable").isEmpty();

        BasePlugin p = new BasePlugin();
        for (Method m : declaredMethods) {
            m.invoke(p, new Object[]{null}); // :/ to mark each method we checked as "covered" in test coverage
        }
    }
}
