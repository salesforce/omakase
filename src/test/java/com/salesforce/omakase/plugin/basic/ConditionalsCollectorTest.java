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

import com.salesforce.omakase.Omakase;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link ConditionalsCollector}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class ConditionalsCollectorTest {
    private static final String SOURCE = ".test {color: red}\n" +
        "\n" +
        "@if(ie7) {\n" +
        "    .test {color: yellow}\n" +
        "}\n" +
        "\n" +
        "@if(IE8) {\n" +
        "    .test {color: green}\n" +
        "}\n" +
        "\n" +
        "@if(!ie9) {\n" +
        "    .test {color: green}\n" +
        "}\n" +
        "\n" +
        "#id, #id2 {font-size: 10em;margin: 10px}\n" +
        "\n" +
        "@if(webkit || blink) {\n" +
        "    #id {border: 1px}\n" +
        "}";

    private ConditionalsCollector collector;

    @Before
    public void setup() {
        collector = new ConditionalsCollector();
        Omakase.source(SOURCE).use(collector).process();
    }

    @Test
    public void foundConditionTrue() {
        assertThat(collector.hasCondition("ie7")).isTrue();
    }

    @Test
    public void foundConditionTrueDifferentCase() {
        assertThat(collector.hasCondition("ie8")).isTrue();
    }

    @Test
    public void foundConditionFalse() {
        assertThat(collector.hasCondition("ie10")).isFalse();
    }

    @Test
    public void testFoundConditions() {
        assertThat(collector.foundConditions()).containsOnly("ie7", "ie8", "ie9", "webkit", "blink");
    }

    @Test
    public void testExcludeNegationOnly() {
        collector = new ConditionalsCollector().excludeNegationOnly(true);
        Omakase.source(SOURCE).use(collector).process();
        assertThat(collector.foundConditions()).containsOnly("ie7", "ie8", "webkit", "blink");
    }
}

