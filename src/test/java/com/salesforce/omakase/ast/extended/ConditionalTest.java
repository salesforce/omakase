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

package com.salesforce.omakase.ast.extended;

import com.salesforce.omakase.plugin.conditionals.ConditionalsConfig;
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
