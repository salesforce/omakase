/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.value.NumericalValue;
import com.salesforce.omakase.ast.declaration.value.NumericalValue.Sign;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link NumericalValue}.
 *
 * @author nmcwilliams
 * @see NumericalValue
 */
public class NumericalValueParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        stream.collectComments();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // parse the optional sign
        Optional<Character> sign = stream.optional(Tokens.SIGN);

        // integer value
        String integerValue = stream.chomp(Tokens.DIGIT);
        Long integer = integerValue.isEmpty() ? null : Long.valueOf(integerValue);

        // decimal
        Long decimal = null;
        if (stream.optionallyPresent(Tokens.DOT)) {
            String decimalValue = stream.chomp(Tokens.DIGIT);
            if (decimalValue.isEmpty()) {
                // there must be a number after a decimal
                throw new ParserException(stream, Message.EXPECTED_DECIMAL);
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
        Optional<String> unit = stream.optionallyPresent(Tokens.PERCENTAGE) ? Optional.of("%") : stream.readIdent();
        if (unit.isPresent()) {
            value.unit(unit.get());
        }

        broadcaster.broadcast(value);
        value.comments(stream.flushComments());
        return true;
    }
}
