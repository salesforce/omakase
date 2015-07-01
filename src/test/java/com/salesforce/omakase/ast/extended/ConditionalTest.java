/*
 * Copyright (C) 2015 salesforce.com, inc.
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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.plugin.basic.ConditionalsConfig;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link Conditional}.
 */
public class ConditionalTest {
    ConditionalsConfig config;

    @Before
    public void setup() {
        config = new ConditionalsConfig();
        config.addTrueConditions("foo");
    }

    @Test
    public void testGetCondition() {
        Conditional conditional = new Conditional("foo", false);
        assertThat(conditional.condition()).isEqualToIgnoringCase("foo");
    }

    @Test
    public void testGetIsNegation() {
        Conditional conditional = new Conditional("foo", true);
        assertThat(conditional.isLogicalNegation()).isTrue();
    }

    @Test
    public void testMatchesTrueWhenPresent() {
        Conditional conditional = new Conditional("foo", false);
        assertThat(conditional.matches(config)).isTrue();
    }

    @Test
    public void testMatchesFalseWithPresentAndNegation() {
        Conditional conditional = new Conditional("foo", true);
        assertThat(conditional.matches(config)).isFalse();
    }

    @Test
    public void testMatchesFalseWhenAbsent() {
        Conditional conditional = new Conditional("bar", false);
        assertThat(conditional.matches(config)).isFalse();
    }

    @Test
    public void testMatchesTrueWhenAbsentAndNegation() {
        Conditional conditional = new Conditional("bar", true);
        assertThat(conditional.matches(config)).isTrue();
    }

    @Test
    public void testWriteWithNegation() {
        Conditional conditional = new Conditional("foo", true);
        assertThat(StyleWriter.inline().writeSnippet(conditional)).isEqualTo("!foo");
    }

    @Test
    public void testWriteWithoutNegation() {
        Conditional conditional = new Conditional("foo", false);
        assertThat(StyleWriter.inline().writeSnippet(conditional)).isEqualTo("foo");
    }
}
