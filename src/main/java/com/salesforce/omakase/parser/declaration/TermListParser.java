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
import com.salesforce.omakase.ast.declaration.value.Term;
import com.salesforce.omakase.ast.declaration.value.TermList;
import com.salesforce.omakase.ast.declaration.value.TermOperator;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.SingleBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link TermList}.
 *
 * @author nmcwilliams
 * @see TermList
 */
public class TermListParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.skipWhitepace();

        // grab the line and column number before parsing anything
        int line = source.line();
        int column = source.column();

        // setup
        TermList termList = null;
        Optional<Term> term;
        Optional<TermOperator> operator = Optional.absent();
        Parser termParser = ParserFactory.termParser();
        SingleBroadcaster<Term> singleTermBroadcaster = new SingleBroadcaster<>(Term.class, broadcaster);

        // try parsing another term until there are no more term operators
        do {
            source.collectComments();

            // try to parse a term
            termParser.parse(source, singleTermBroadcaster.reset());
            term = singleTermBroadcaster.broadcasted();

            // if we have a term, add it to the list
            if (term.isPresent()) {
                // delayed creation of the term list
                if (termList == null) {
                    termList = new TermList(line, column);
                }

                // add the previous operator as a member to term list before adding the term
                if (operator.isPresent()) {
                    termList.add(operator.get());
                }

                // add the term to the list
                termList.add(term.get());

                // try to parse another term operator. The presence of a space *could* be the "single space" term
                // operator. Or it could just be whitespace around another term operator.
                source.collectComments(false);
                boolean mightBeSpaceOperator = source.optionallyPresent(Tokens.WHITESPACE);

                // if we already know that a space is present, we must skip past all other whitespace
                if (mightBeSpaceOperator) {
                    source.skipWhitepace();
                }

                // after we've already checked for the single space operator, it's ok to consume comments and surrounding
                // whitespace.
                source.collectComments();

                // see if there is an actual non-space operator
                operator = source.optionalFromEnum(TermOperator.class);

                // if no operator is parsed and we parsed at least one space then we know it's a single space operator
                if (mightBeSpaceOperator && !operator.isPresent()) {
                    operator = Optional.of(TermOperator.SPACE);
                }
            } else {
                // if we didn't find a term but we did find a non-space operator then it's an erroneous trailing operator
                if (operator.isPresent() && operator.get() != TermOperator.SPACE) {
                    throw new ParserException(source, Message.TRAILING_OPERATOR, operator.get());
                }
                operator = Optional.absent();
            }
        } while (operator.isPresent());

        // if no terms were parsed then return false
        if (termList == null) return false;

        // check for !important
        termList.important(ParserFactory.importantParser().parse(source, broadcaster));

        // broadcast the new term list
        broadcaster.broadcast(termList);
        return true;
    }
}
