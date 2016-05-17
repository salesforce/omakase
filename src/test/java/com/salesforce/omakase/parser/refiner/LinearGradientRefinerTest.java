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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link LinearGradientRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class LinearGradientRefinerTest {
    private LinearGradientRefiner linearRefiner;
    private QueryableBroadcaster broadcaster;
    private MasterRefiner refiner;

    @Before
    public void setup() {
        linearRefiner = new LinearGradientRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new MasterRefiner(broadcaster).register(linearRefiner);
    }

    @Test
    public void returnsFalseForNonMatchingFunction() {
        RawFunction functionValue = new RawFunction(5, 2, "blah", "blah");
        assertThat(linearRefiner.refine(functionValue, broadcaster, refiner)).isSameAs(Refinement.NONE);
    }

    @Test
    public void returnsTrueForLinearGradient() {
        RawFunction functionValue = new RawFunction(1, 1, "linear-gradient", "red, yellow");
        linearRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.findOnly(LinearGradientFunctionValue.class).isPresent()).isTrue();
    }

    @Test
    public void returnsTrueForRepeatingLinearGradient() {
        RawFunction functionValue = new RawFunction(1, 1, "repeating-linear-gradient", "red, yellow");
        linearRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.findOnly(LinearGradientFunctionValue.class).isPresent()).isTrue();
    }

    @Test
    public void setsRepeatingLinearGradientToTrue() {
        RawFunction functionValue = new RawFunction(1, 1, "repeating-linear-gradient", "red, yellow");
        linearRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.findOnly(LinearGradientFunctionValue.class).get().repeating()).isTrue();
    }

    @Test
    public void assignsCorrectLineAndColumn() {
        RawFunction functionValue = new RawFunction(5, 2, "linear-gradient", "red, yellow");
        linearRefiner.refine(functionValue, broadcaster, refiner);
        LinearGradientFunctionValue value = broadcaster.findOnly(LinearGradientFunctionValue.class).get();
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }
}
