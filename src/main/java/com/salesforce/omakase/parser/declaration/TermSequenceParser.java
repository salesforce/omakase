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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.declaration.TermList;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueuingBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Parses a sequence of both {@link Term}s <em>and</em> {@link Operator}s.
 * <p/>
 * This does not parts importants or broadcast a {@link TermList}.
 *
 * @author nmcwilliams
 * @see TermListParser
 */
public final class TermSequenceParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        // gather comments and skip whitespace
        source.collectComments();

        boolean matchedAny = false;
        boolean matchedThisTime = false;

        QueuingBroadcaster queue = new QueuingBroadcaster(broadcaster).pause();

        // we want to let RawFunctions through so that they can be altered before refinement
        queue.alwaysFlush(RawFunction.class);

        do {
            matchedThisTime = ParserFactory.termParser().parse(source, queue, refiner);
            matchedAny = matchedAny || matchedThisTime;
        } while (matchedThisTime && !source.eof() && ParserFactory.operatorParser().parse(source, queue, refiner));

        // check for a trailing operator. if it's a space operator then we want to remove it
        Broadcastable last = queue.peekLast();
        if (last instanceof Operator) {
            if (((Operator)last).type() != OperatorType.SPACE) throw new ParserException(source, Message.TRAILING_OPERATOR, last);
            queue.reject(last);
        }

        // flush the queue
        queue.resume();

        return matchedAny;
    }

}
