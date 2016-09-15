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
        Source source = new Source(raw.args().trim(), raw.line(), raw.column());
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
