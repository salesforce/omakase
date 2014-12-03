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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.QuotationMode;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Refines {@link RawFunction}s to {@link UrlFunctionValue}s.
 *
 * @author nmcwilliams
 * @see UrlFunctionValue
 */
public final class UrlRefiner implements FunctionRefiner {
    private static final String NAME = "url";

    @Override
    public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
        if (!raw.name().equals(NAME)) return Refinement.NONE;

        // check for quotes
        Source source = new Source(raw.args(), raw.line(), raw.column());
        QuotationMode mode = null;

        if (Tokens.DOUBLE_QUOTE.matches(source.current())) {
            mode = QuotationMode.DOUBLE;
        } else if (Tokens.SINGLE_QUOTE.matches(source.current())) {
            mode = QuotationMode.SINGLE;
        }

        // get the url content
        String args = (mode != null) ? source.readString().get() : raw.args();

        // there shouldn't be any content after a closing quote
        if (mode != null && !source.eof()) {
            throw new ParserException(raw, Message.UNEXPECTED_AFTER_QUOTE, source.toString());
        }

        // create the value object and broadcast it
        UrlFunctionValue url = new UrlFunctionValue(raw.line(), raw.column(), args);
        url.quotationMode(mode);
        broadcaster.broadcast(url);

        return Refinement.FULL;
    }
}
