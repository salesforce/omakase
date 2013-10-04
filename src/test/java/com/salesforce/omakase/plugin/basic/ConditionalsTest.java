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

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link Conditionals}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsTest {
    @Test
    public void addCondition() {
        Conditionals conditionals = new Conditionals();
        conditionals.addTrueConditions(Sets.newHashSet("ie7"));
        assertThat(conditionals.trueConditions()).hasSize(1);
        assertThat(Iterables.get(conditionals.trueConditions(), 0)).isEqualTo("ie7");
    }

    @Test
    public void removeCondition() {
        Conditionals conditionals = new Conditionals("ie7");
        assertThat(conditionals.trueConditions()).hasSize(1);
        assertThat(Iterables.get(conditionals.trueConditions(), 0)).isEqualTo("ie7");
        conditionals.removeCondition("ie7");
        assertThat(conditionals.trueConditions()).isEmpty();
    }

    @Test
    public void clearConditions() {
        Conditionals conditionals = new Conditionals(Sets.newHashSet("ie7", "ie8"));
        assertThat(conditionals.trueConditions()).hasSize(2);
        conditionals.clearTrueConditions();
        assertThat(conditionals.trueConditions()).isEmpty();
    }
}
