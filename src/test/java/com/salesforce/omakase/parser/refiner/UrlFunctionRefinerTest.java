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
import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.declaration.value.UrlFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

/**
 * Unit tests for {@link UrlFunctionRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UrlFunctionRefinerTest {
    private UrlFunctionRefiner urlRefiner;
    private QueryableBroadcaster broadcaster;
    private Refiner refiner;

    @Before
    public void setup() {
        urlRefiner = new UrlFunctionRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new Refiner(broadcaster, ImmutableSet.<RefinerStrategy>of(urlRefiner));
    }

    @Test
    public void returnsFalseForNonMatchingFunction() {
        FunctionValue functionValue = new FunctionValue("blah", "blah");
        assertThat(urlRefiner.refine(functionValue, broadcaster, refiner)).isFalse();
    }

    @Test
    public void refinesToUrlFunctionValueInstance() {
        FunctionValue functionValue = new FunctionValue("url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(functionValue.refinedValue().get()).isInstanceOf(UrlFunctionValue.class);
    }

    @Test
    public void assignsCorrectLineAndColumn() {
        fail("unimplemented");
    }

    @Test
    public void errorIfMissingClosingQuote() {
        fail("unimplemented");
    }

    @Test
    public void correctlyFindsDoubleQuotes() {
        fail("unimplemented");
    }

    @Test
    public void correctFindsSingleQuotes() {
        fail("unimplemented");
    }

    @Test
    public void correctlyGetsArgsWhenQuotesPresent() {
        fail("unimplemented");
    }

    @Test
    public void correctlyGetsArgsWhenQuotesNotPresent() {
        FunctionValue functionValue = new FunctionValue("url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(((UrlFunctionValue)functionValue.refinedValue().get()).url()).isEqualTo("/imgs/poof.png");
    }

    @Test
    public void broadcastsUrlFunctionValue() {
        FunctionValue functionValue = new FunctionValue("url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.all()).hasSize(1);
        assertThat(Iterables.get(broadcaster.all(), 0)).isInstanceOf(UrlFunctionValue.class);
    }
}
