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

package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.NumericalValue.Sign;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link NumericalValue}.
 *
 * @author nmcwilliams
 * @see NumericalValue
 */
public final class NumericalValueParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

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
            unit = string.isEmpty() ? Optional.<String>absent() : Optional.of(string);
        }

        if (unit.isPresent()) {
            numerical.unit(unit.get());
        }

        broadcaster.broadcast(numerical);
        numerical.comments(source.flushComments());
        return true;
    }
}
