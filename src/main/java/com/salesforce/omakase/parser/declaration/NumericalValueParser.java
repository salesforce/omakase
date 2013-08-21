/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue.Sign;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.*;
import com.salesforce.omakase.parser.Stream.Snapshot;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link NumericalValue}.
 * 
 * @author nmcwilliams
 */
public class NumericalValueParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        Snapshot snapshot = stream.snapshot();

        // parse the optional sign
        Optional<Character> sign = stream.optional(Tokens.SIGN);

        // integer value
        String integerValue = stream.chomp(Tokens.DIGIT);
        Integer integer = integerValue.isEmpty() ? null : Integer.valueOf(integerValue);

        // decimal
        Integer decimal = null;
        if (stream.optionallyPresent(Tokens.DOT)) {
            String decimalValue = stream.chomp(Tokens.DIGIT);
            if (decimalValue.isEmpty()) {
                // there must be a number after a decimal
                throw new ParserException(stream, Message.EXPECTED_DECIMAL);
            }
            decimal = Integer.valueOf(decimalValue);
        }

        // integer value or decimal must be present
        if (integer == null && decimal == null) return stream.rollback();

        // create the numerical value instance
        int realIntegerValue = integer == null ? 0 : integer;
        NumericalValue value = new NumericalValue(snapshot.line, snapshot.column, realIntegerValue);

        // add the decimal value if applicable
        if (decimal != null) {
            value.decimalValue(decimal);
        }

        // add the sign if applicable
        if (sign.isPresent()) {
            value.explicitSign(sign.get().equals('-') ? Sign.NEGATIVE : Sign.POSITIVE);
        }

        // check for a unit
        Optional<String> unit = stream.readIdent();
        if (unit.isPresent()) {
            value.unit(unit.get());
        }

        broadcaster.broadcast(SubscriptionType.CREATED, value);
        return true;
    }
}
