/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.Broadcaster;
import com.salesforce.omakase.ast.declaration.value.*;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses an {@link TermList}.
 * 
 * @author nmcwilliams
 */
public class TermListParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // grab the line and column number before parsing anything
        int line = stream.line();
        int column = stream.column();

        Term term = null;
        TermList expression = null;
        TermOperator operator = null;

        do {
            stream.skipWhitepace();
            term = null;

            // XXX once i have bigger test sheets, see if changing the order of these around improves perf

            // numerical values
            Optional<NumericalValue> number = stream.readNumber();
            if (number.isPresent()) {
                NumericalValue value = number.get();

                // check for a unit
                Optional<String> unit = stream.readIdent();
                if (unit.isPresent()) {
                    value.unit(unit.get());
                }

                term = value;
            }

            // function (must be before keyword)
            if (term == null) {
                Optional<FunctionValue> function = stream.readFunction();
                if (function.isPresent()) {
                    term = function.get();
                }
            }

            // keywords
            if (term == null) {
                Optional<String> keyword = stream.readIdent();
                if (keyword.isPresent()) {
                    term = new KeywordValue(keyword.get());
                }
            }

            // hex
            if (term == null) {
                Optional<HexColorValue> color = stream.readHexColor();
                if (color.isPresent()) {
                    term = color.get();
                }
            }

            // string
            if (term == null) {
                // TODO
            }

            // if we have a term, add it to the list
            if (term != null) {
                if (expression == null) {
                    expression = new TermList(line, column);
                }
                expression.add(term);

                // try to parse an operator
                if (stream.optionallyPresent(Tokens.SINGLE_SPACE)) {
                    operator = TermOperator.SINGLE_SPACE;
                } else if (stream.optionallyPresent(Tokens.COMMA)) {
                    operator = TermOperator.COMMA;
                } else if (stream.optionallyPresent(Tokens.FORWARD_SLASH)) {
                    operator = TermOperator.SLASH;
                } else {
                    operator = null;
                }

                // add the operator as a member to term list, so we know what to print out
                if (operator != null) {
                    expression.add(operator);
                }
            }

        } while (operator != null);

        // if any term was parsed then broadcast the term list
        if (expression == null) return false;

        broadcaster.broadcast(SubscriptionType.CREATED, expression);
        return true;
    }
}
