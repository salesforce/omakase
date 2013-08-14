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
 * TODO Description
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
        TermList list = null;
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
            term = (number.isPresent()) ? number.get() : null;

            // keywords
            if (term == null) {
                Optional<String> keyword = stream.readIdent();
                if (keyword.isPresent()) {
                    term = new KeywordValue(keyword.get());
                }
            }

            // string
            if (term == null) {

            }

            // hex
            if (term == null) {

            }

            // function
            if (term == null) {

            }

            // if we have a term, add it to the list
            if (term != null) {
                if (list == null) {
                    list = new TermList(line, column);
                }
                list.add(term);

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

                if (operator != null) {
                    list.add(operator);
                }
            }

        } while (operator != null);

        // if any term was parsed then broadcast the term list
        if (list == null) return false;

        broadcaster.broadcast(SubscriptionType.CREATED, list);
        return true;
    }
}
