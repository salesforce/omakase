/*
 * Copyright (c) 2017, salesforce.com, inc.
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

package com.salesforce.omakase.plugin.syntax;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.Grammar;

/**
 * Unit tests for {@link LinearGradientPlugin}.
 *
 * @author nmcwilliams
 */
public class LinearGradientPluginTest {
    private LinearGradientPlugin plugin;
    private QueryableBroadcaster broadcaster;

    @Before
    public void setup() {
        plugin = new LinearGradientPlugin();
        broadcaster = new QueryableBroadcaster();
    }

    @Test
    public void returnsFalseForNonMatchingFunction() {
        RawFunction functionValue = new RawFunction(5, 2, "blah", "blah");

        plugin.refine(functionValue, new Grammar(), broadcaster);
        assertThat(broadcaster.count()).isEqualTo(0);
    }

    @Test
    public void normalLinearGradient() {
        RawFunction functionValue = new RawFunction(5, 2, "linear-gradient", "red, yellow");

        plugin.refine(functionValue, new Grammar(), broadcaster);
        Optional<LinearGradientFunctionValue> f = broadcaster.find(LinearGradientFunctionValue.class);
        assertThat(f.isPresent()).isTrue();
        assertThat(f.get().repeating()).isFalse();
        assertThat(f.get().args()).isEqualTo("red, yellow");
        assertThat(f.get().line()).isEqualTo(5);
        assertThat(f.get().column()).isEqualTo(2);
    }

    @Test
    public void repeatingLinearGradient() {
        RawFunction functionValue = new RawFunction(1, 1, "repeating-linear-gradient", "red, yellow");

        plugin.refine(functionValue, new Grammar(), broadcaster);
        Optional<LinearGradientFunctionValue> f = broadcaster.find(LinearGradientFunctionValue.class);
        assertThat(f.isPresent()).isTrue();
        assertThat(f.get().repeating()).isTrue();
        assertThat(f.get().args()).isEqualTo("red, yellow");
    }
}
