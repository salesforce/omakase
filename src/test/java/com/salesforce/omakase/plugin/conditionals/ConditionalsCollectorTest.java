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

