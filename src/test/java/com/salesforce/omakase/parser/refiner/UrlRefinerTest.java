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

import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.ParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Unit tests for {@link UrlRefiner}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class UrlRefinerTest {
    @Rule public final ExpectedException exception = ExpectedException.none();

    private UrlRefiner urlRefiner;
    private QueryableBroadcaster broadcaster;
    private MasterRefiner refiner;

    @Before
    public void setup() {
        urlRefiner = new UrlRefiner();
        broadcaster = new QueryableBroadcaster();
        refiner = new MasterRefiner(broadcaster).register(urlRefiner);

    }

    @Test
    public void returnsFalseForNonMatchingFunction() {
        RawFunction functionValue = new RawFunction(5, 2, "blah", "blah");
        assertThat(urlRefiner.refine(functionValue, broadcaster, refiner)).isFalse();
    }

    @Test
    public void refinesToUrlFunctionValueInstance() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        assertThat(broadcaster.findOnly(UrlFunctionValue.class).isPresent()).isTrue();
    }

    @Test
    public void assignsCorrectLineAndColumn() {
        RawFunction functionValue = new RawFunction(5, 2, "url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = broadcaster.findOnly(UrlFunctionValue.class).get();
        assertThat(value.line()).isEqualTo(5);
        assertThat(value.column()).isEqualTo(2);
    }

    @Test
    public void errorIfMissingClosingQuote() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "\"/imgs/poof.png");
        exception.expect(ParserException.class);
        urlRefiner.refine(functionValue, broadcaster, refiner);
    }

    @Test
    public void correctlyFindsDoubleQuotes() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "\"/imgs/poof.png\"");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = broadcaster.findOnly(UrlFunctionValue.class).get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.DOUBLE);
    }

    @Test
    public void correctFindsSingleQuotes() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "'/imgs/poof.png'");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = broadcaster.findOnly(UrlFunctionValue.class).get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().get()).isEqualTo(QuotationMode.SINGLE);
    }

    @Test
    public void correctlyFindsNoQuotes() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "/imgs/poof.png");
        urlRefiner.refine(functionValue, broadcaster, refiner);
        UrlFunctionValue value = broadcaster.findOnly(UrlFunctionValue.class).get();
        assertThat(value.url()).isEqualTo("/imgs/poof.png");
        assertThat(value.quotationMode().isPresent()).isFalse();
    }

    @Test
    public void errorsIfContentAfterClosingQuote() {
        RawFunction functionValue = new RawFunction(1, 1, "url", "\"/imgs/poof.png\"aaa");
        exception.expect(ParserException.class);
        exception.expectMessage("Unexpected content in url after closing quote");
        urlRefiner.refine(functionValue, broadcaster, refiner);
    }
}
