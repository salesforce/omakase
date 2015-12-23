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

package com.salesforce.omakase.plugin.conditionals;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.salesforce.omakase.plugin.conditionals.ConditionalsConfig;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConditionalsConfig}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsConfigTest {
    @Test
    public void addCondition() {
        ConditionalsConfig manager = new ConditionalsConfig();
        manager.addTrueConditions(Sets.newHashSet("ie7"));
        assertThat(manager.trueConditions()).hasSize(1);
        assertThat(Iterables.get(manager.trueConditions(), 0)).isEqualTo("ie7");
    }

    @Test
    public void hasConditionTrue() {
        ConditionalsConfig manager = new ConditionalsConfig();
        manager.addTrueConditions("ie7");
        assertThat(manager.hasCondition("ie7")).isTrue();
    }

    @Test
    public void hasConditionFalse() {
        ConditionalsConfig manager = new ConditionalsConfig();
        manager.addTrueConditions("ie7");
        assertThat(manager.hasCondition("ie8")).isFalse();
    }

    @Test
    public void removeCondition() {
        ConditionalsConfig manager = new ConditionalsConfig().addTrueConditions("ie7");
        assertThat(manager.trueConditions()).hasSize(1);
        assertThat(Iterables.get(manager.trueConditions(), 0)).isEqualTo("ie7");
        manager.removeTrueCondition("ie7");
        assertThat(manager.trueConditions()).isEmpty();
    }

    @Test
    public void clearConditions() {
        ConditionalsConfig manager = new ConditionalsConfig().addTrueConditions("ie7", "ie8");
        assertThat(manager.trueConditions()).hasSize(2);
        manager.clearTrueConditions();
        assertThat(manager.trueConditions()).isEmpty();
    }

    @Test
    public void replacesConditions() {
        ConditionalsConfig manager = new ConditionalsConfig().addTrueConditions("ie7", "ie8");
        assertThat(manager.replaceTrueConditions("chrome").trueConditions()).containsExactly("chrome");
    }

    @Test
    public void passthroughModeDefaultFalse() {
        ConditionalsConfig manager = new ConditionalsConfig();
        assertThat(manager.isPassthroughMode()).isFalse();
    }

    @Test
    public void passthroughModeTrue() {
        ConditionalsConfig manager = new ConditionalsConfig().passthroughMode(true);
        assertThat(manager.isPassthroughMode()).isTrue();
    }
}
