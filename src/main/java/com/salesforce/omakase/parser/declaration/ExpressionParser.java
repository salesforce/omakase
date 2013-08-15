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
 * Parses an {@link Expression}.
 * 
 * @author nmcwilliams
 */
public class ExpressionParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();

        // grab the line and column number before parsing anything
        int line = stream.line();
        int column = stream.column();

        ExpressionTerm term = null;
        Expression expression = null;
        ExpressionOperator operator = null;

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

            // function (must be before keyword
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
                Optional<String> color = stream.readHexColor();
                if (color.isPresent()) {
                    term = new HexColorValue(color.get());
                }
            }

            // string
            if (term == null) {

            }
            // if we have a term, add it to the list
            if (term != null) {
                if (expression == null) {
                    expression = new Expression(line, column);
                }
                expression.add(term);

                // try to parse an operator
                if (stream.optionallyPresent(Tokens.SINGLE_SPACE)) {
                    operator = ExpressionOperator.SINGLE_SPACE;
                } else if (stream.optionallyPresent(Tokens.COMMA)) {
                    operator = ExpressionOperator.COMMA;
                } else if (stream.optionallyPresent(Tokens.FORWARD_SLASH)) {
                    operator = ExpressionOperator.SLASH;
                } else {
                    operator = null;
                }

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
