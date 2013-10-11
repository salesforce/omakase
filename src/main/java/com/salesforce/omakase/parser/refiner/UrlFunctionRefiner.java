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

import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.ast.declaration.value.QuotationMode;
import com.salesforce.omakase.ast.declaration.value.UrlFunctionValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Refines {@link FunctionValue}s to {@link UrlFunctionValue}s.
 *
 * @author nmcwilliams
 * @see UrlFunctionValue
 */
public class UrlFunctionRefiner implements FunctionValueRefinerStrategy {
    private static final String NAME = "url";

    @Override
    public boolean refine(FunctionValue functionValue, Broadcaster broadcaster, Refiner refiner) {
        if (!functionValue.name().equals(NAME)) return false;

        // check for quotes
        Source source = new Source(functionValue.args());
        QuotationMode mode = null;

        if (Tokens.DOUBLE_QUOTE.matches(source.current())) {
            mode = QuotationMode.DOUBLE;
        } else if (Tokens.SINGLE_QUOTE.matches(source.current())) {
            mode = QuotationMode.SINGLE;
        }

        // TODO error if "multiple args?"

        // get the url content
        String args = (mode != null) ? source.readString().get() : functionValue.args();

        // create the value object
        UrlFunctionValue url = new UrlFunctionValue(functionValue.line(), functionValue.column(), args);
        url.quotationMode(mode);

        // broadcast it
        broadcaster.broadcast(url);

        // add it to the function value
        functionValue.refinedValue(url);

        return true;
    }
}
