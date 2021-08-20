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

package com.salesforce.omakase.parser.declaration;

import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.NumericalValue.Sign;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link NumericalValue}.
 *
 * @author nmcwilliams
 * @see NumericalValue
 */
public final class NumericalValueParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        // move past comments and whitespace
        source.collectComments();

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // parse the optional sign
        Optional<Character> sign = source.optional(Tokens.SIGN);

        // begin parsing the number
        StringBuilder value = null;

        // integer value
        String integerValue = source.chomp(Tokens.DIGIT);
        if (!integerValue.isEmpty()) value = new StringBuilder(integerValue);

        // decimal
        if (source.optionallyPresent(Tokens.DOT)) {
            if (value == null) value = new StringBuilder();
            value.append('.');

            // there must be a number after a decimal point
            String decimalValue = source.chomp(Tokens.DIGIT);
            if (decimalValue.isEmpty()) throw new ParserException(source, Message.EXPECTED_DECIMAL);
            value.append(decimalValue);
        }

        // integer value or decimal must be present
        if (value == null) return snapshot.rollback();

        // create the numerical value instance
        NumericalValue numerical = new NumericalValue(snapshot.originalLine, snapshot.originalColumn, value.toString());

        // add the sign if applicable
        if (sign.isPresent()) {
            //noinspection UnnecessaryUnboxing
            numerical.explicitSign(sign.get().charValue() == '-' ? Sign.NEGATIVE : Sign.POSITIVE);
        }

        // check for a unit (% or alpha)
        Optional<String> unit;

        if (source.optionallyPresent(Tokens.PERCENTAGE)) {
            unit = Optional.of("%");
        } else {
            String string = source.chomp(Tokens.ALPHA);
            unit = string.isEmpty() ? Optional.empty() : Optional.of(string);
        }

        if (unit.isPresent()) {
            numerical.unit(unit.get());
        }

        broadcaster.broadcast(numerical);
        numerical.comments(source.flushComments());
        return true;
    }

}
