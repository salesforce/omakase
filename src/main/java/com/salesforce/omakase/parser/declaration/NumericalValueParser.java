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
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue.Sign;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link NumericalValue}.
 *
 * @author nmcwilliams
 * @see NumericalValue
 */
public class NumericalValueParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // snapshot the current state before parsing
        Source.Snapshot snapshot = source.snapshot();

        // parse the optional sign
        Optional<Character> sign = source.optional(Tokens.SIGN);

        // integer value
        String integerValue = source.chomp(Tokens.DIGIT);
        Long integer = integerValue.isEmpty() ? null : Long.valueOf(integerValue);

        // decimal
        Long decimal = null;
        if (source.optionallyPresent(Tokens.DOT)) {
            String decimalValue = source.chomp(Tokens.DIGIT);
            if (decimalValue.isEmpty()) {
                // there must be a number after a decimal
                throw new ParserException(source, Message.EXPECTED_DECIMAL);
            }
            decimal = Long.valueOf(decimalValue);
        }

        // integer value or decimal must be present
        if (integer == null && decimal == null) return snapshot.rollback();

        // create the numerical value instance
        Long realIntegerValue = integer == null ? 0 : integer;
        NumericalValue value = new NumericalValue(snapshot.line, snapshot.column, realIntegerValue);

        // add the decimal value if applicable
        if (decimal != null) {
            value.decimalValue(decimal);
        }

        // add the sign if applicable
        if (sign.isPresent()) {
            value.explicitSign(sign.get().equals('-') ? Sign.NEGATIVE : Sign.POSITIVE);
        }

        // check for a % or unit
        Optional<String> unit = source.optionallyPresent(Tokens.PERCENTAGE) ? Optional.of("%") : source.readIdent();
        if (unit.isPresent()) {
            value.unit(unit.get());
        }

        broadcaster.broadcast(value);
        value.comments(source.flushComments());
        return true;
    }
}
