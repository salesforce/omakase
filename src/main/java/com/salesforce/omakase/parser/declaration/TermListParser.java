/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.value.Term;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.ast.declaration.value.TermOperator;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.*;

/**
 * Parses a {@link TermList}.
 * 
 * @author nmcwilliams
 */
public class TermListParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        stream.skipWhitepace();
        stream.rejectComments();

        // grab the line and column number before parsing anything
        int line = stream.line();
        int column = stream.column();

        // setup
        TermList termList = null;
        Optional<TermOperator> operator = Optional.absent();
        Parser termParser = ParserFactory.termParser();

        // try parsing another term until there are no more term operators
        do {
            // whitespace should only be skipped at the beginning of a term
            stream.skipWhitepace();

            // try to parse a term
            QueryableBroadcaster collector = new QueryableBroadcaster(broadcaster);
            termParser.parse(stream, collector);
            Optional<Term> term = collector.findOnly(Term.class);

            // if we have a term, add it to the list
            if (term.isPresent()) {
                // delayed creation of the term list
                if (termList == null) {
                    termList = new TermList(line, column);
                }

                // add the term to the list
                termList.add(term.get());

                // try to parse a term operator
                operator = stream.optionalFromEnum(TermOperator.class);

                // add the operator as a member to term list, so we know what to print out
                if (operator.isPresent()) {
                    termList.add(operator.get());
                }
            } else {
                operator = Optional.absent();
            }
        } while (operator.isPresent());

        // if any term was parsed then broadcast the term list
        if (termList == null) return false;

        // allow comments again
        stream.enableComments();

        // broadcast the new term list
        broadcaster.broadcast(SubscriptionType.CREATED, termList);
        return true;
    }
}
