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

import com.google.common.collect.Lists;
import com.salesforce.omakase.emitter.Subscribable;
import org.junit.Test;
import org.reflections.Reflections;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link BasePlugin}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class BasePluginTest {
    @Test
    public void hasMethodForEverySubscribable() {
        int numMethods = BasePlugin.class.getDeclaredMethods().length;

        Reflections reflections = new Reflections("com.salesforce.omakase.ast");
        int expected = Lists.newArrayList(reflections.getTypesAnnotatedWith(Subscribable.class)).size();

        assertThat(numMethods)
            .overridingErrorMessage("BasePlugin.java must have a subscription method for each subscribable syntax type")
            .isEqualTo(expected);
    }
}
