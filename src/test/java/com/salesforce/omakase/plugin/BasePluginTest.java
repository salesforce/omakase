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

package com.salesforce.omakase.plugin;

import com.google.common.collect.Sets;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
        List<Method> methods = new ArrayList<>();
        for (Method method : declaredMethods) {
            if (!method.isSynthetic()) {
                subscriptions.add(method.getParameterTypes()[0]);
                methods.add(method);
            }
        }

        Reflections reflections = new Reflections("com.salesforce.omakase.ast");
        Set<Class<?>> subscribables = reflections.getTypesAnnotatedWith(Subscribable.class, true);

        assertThat(subscriptions).describedAs("BasePlugin is missing subscribable units").containsAll(subscribables);

        Sets.SetView<Class<?>> difference = Sets.difference(subscriptions, subscribables);
        assertThat(difference).describedAs("BasePlugin has a subscription to an object that is not subscribable").isEmpty();

        BasePlugin p = new BasePlugin();
        for (Method m : methods) {
            m.invoke(p, new Object[]{null}); // :/ to mark each method we checked as "covered" in test coverage
        }
    }
}
