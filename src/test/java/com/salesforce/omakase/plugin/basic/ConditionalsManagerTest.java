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
import com.salesforce.omakase.test.util.Util;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConditionalsManager}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsManagerTest {
    @Test
    public void addCondition() {
        ConditionalsManager manager = new ConditionalsManager();
        manager.addTrueConditions(Sets.newHashSet("ie7"));
        assertThat(manager.trueConditions()).hasSize(1);
        assertThat(Iterables.get(manager.trueConditions(), 0)).isEqualTo("ie7");
    }

    @Test
    public void hasConditionTrue() {
        ConditionalsManager manager = new ConditionalsManager();
        manager.addTrueConditions("ie7");
        assertThat(manager.hasCondition("ie7")).isTrue();
    }

    @Test
    public void hasConditionFalse() {
        ConditionalsManager manager = new ConditionalsManager();
        manager.addTrueConditions("ie7");
        assertThat(manager.hasCondition("ie8")).isFalse();
    }

    @Test
    public void removeCondition() {
        ConditionalsManager manager = new ConditionalsManager().addTrueConditions("ie7");
        assertThat(manager.trueConditions()).hasSize(1);
        assertThat(Iterables.get(manager.trueConditions(), 0)).isEqualTo("ie7");
        manager.removeTrueCondition("ie7");
        assertThat(manager.trueConditions()).isEmpty();
    }

    @Test
    public void clearConditions() {
        ConditionalsManager manager = new ConditionalsManager().addTrueConditions("ie7", "ie8");
        assertThat(manager.trueConditions()).hasSize(2);
        manager.clearTrueConditions();
        assertThat(manager.trueConditions()).isEmpty();
    }

    @Test
    public void passthroughModeDefaultFalse() {
        ConditionalsManager manager = new ConditionalsManager();
        assertThat(manager.isPassthroughMode()).isFalse();
    }

    @Test
    public void passthroughModeTrue() {
        ConditionalsManager manager = new ConditionalsManager().passthroughMode(true);
        assertThat(manager.isPassthroughMode()).isTrue();
    }

    @Test
    public void toStringTest() {
        ConditionalsManager manager = new ConditionalsManager();
        assertThat(manager.toString()).isNotEqualTo(Util.originalToString(manager));
    }
}
