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
import com.salesforce.omakase.ast.declaration.value.QuotationMode;
import com.salesforce.omakase.ast.declaration.value.UrlFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UrlFunctionRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UrlFunctionRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

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
        FunctionValue functionValue = new FunctionValue(5, 2, "url", "/imgs/poof.png", refiner);
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = (UrlFunctionValue)functionValue.refinedValue().get();
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void errorIfMissingClosingQuote() {
        FunctionValue functionValue = new FunctionValue("url", "\"/imgs/poof.png");
        exception.expect(ParserException.class);
        urlRefiner.refine(functionValue, broadcaster, refiner);
    }

    @Test
    public void correctlyFindsDoubleQuotes() {
        FunctionValue functionValue = new FunctionValue("url", "\"/imgs/poof.png\"");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = (UrlFunctionValue)functionValue.refinedValue().get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.DOUBLE);
    }

    @Test
    public void correctFindsSingleQuotes() {
        FunctionValue functionValue = new FunctionValue("url", "'/imgs/poof.png'");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = (UrlFunctionValue)functionValue.refinedValue().get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.SINGLE);
    }

    @Test
    public void correctlyFindsNoQuotes() {
        FunctionValue functionValue = new FunctionValue("url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = (UrlFunctionValue)functionValue.refinedValue().get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().isPresent()).isFalse();
    }

    @Test
    public void broadcastsUrlFunctionValue() {
        FunctionValue functionValue = new FunctionValue("url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.all()).hasSize(1);
        assertThat(Iterables.get(broadcaster.all(), 0)).isInstanceOf(UrlFunctionValue.class);
    }

    @Test
    public void errorsIfContentAfterClosingQuote() {
        FunctionValue functionValue = new FunctionValue("url", "\"/imgs/poof.png\"aaa");
        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected content in url after closing quote");
        urlRefiner.refine(functionValue, broadcaster, refiner);
    }
}
