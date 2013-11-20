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

package com.salesforce.omakase.parser.refiner;

import com.google.common.collect.ImmutableSet;
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
    private Refiner refiner;

    @Before
    public void setup() {
        linearRefiner = new LinearGradientRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new Refiner(broadcaster, ImmutableSet.<RefinerStrategy>of(linearRefiner));
    }

    @Test
    public void returnsFalseForNonMatchingFunction() {
        RawFunction functionValue = new RawFunction(5, 2, "blah", "blah");
        assertThat(linearRefiner.refine(functionValue, broadcaster, refiner)).isFalse();
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
