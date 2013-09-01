/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.value.Term;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.ast.declaration.value.TermOperator;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.parser.*;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link TermList}.
 * 
 * @see TermList
 * @see TermListParserTest
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

        QueryableBroadcaster collector = null;
        Optional<Term> term = null;

        // TODO complicated logic needs to be broken up. Make it similar to how complex selector is done

        // try parsing another term until there are no more term operators
        do {
            // whitespace should only be skipped at the beginning of a term, otherwise we could accidently skip over a
            // term operator.
            stream.skipWhitepace();

            // try to parse a term
            collector = new QueryableBroadcaster(broadcaster);
            termParser.parse(stream, collector);
            term = collector.findOnly(Term.class);

            // if we have a term, add it to the list
            if (term.isPresent()) {
                // add the previous operator as a member to term list
                if (operator.isPresent()) {
                    termList.add(operator.get());
                }

                // delayed creation of the term list
                if (termList == null) {
                    termList = new TermList(line, column);
                }

                // add the term to the list
                termList.add(term.get());

                // try to parse another term operator. The presence of a space *could* be the "single space" term
                // operator. Or it could just be whitespace around another term operator
                boolean mightBeSpaceOperator = stream.optionallyPresent(Tokens.WHITESPACE);

                if (mightBeSpaceOperator) {
                    // if we already know that a space is present, we must skip past all other whitespace
                    stream.skipWhitepace();
                }

                operator = stream.optionalFromEnum(TermOperator.class);

                // if no operator is parsed and we parsed at least one space then we know it's a single space operator
                if (mightBeSpaceOperator && !operator.isPresent()) {
                    operator = Optional.of(TermOperator.SPACE);
                }
            } else {
                if (operator.isPresent() && operator.get() != TermOperator.SPACE) {
                    // it's a trailing operator
                    throw new ParserException(stream, Message.EXPECTED_TERM, operator.get());
                }
                operator = Optional.absent();
            }
        } while (operator.isPresent());

        // if no terms were parsed then return false
        if (termList == null) return false;

        // allow comments again
        stream.enableComments();

        // broadcast the new term list
        broadcaster.broadcast(termList);
        return true;
    }
}
