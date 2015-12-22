/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a single term {@link Operator}.
 *
 * @author nmcwilliams
 * @see Operator
 */
public final class OperatorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // skip comments at the beginning but not whitespace
        source.collectComments(false);

        // save off the current position before parsing any content
        int line = source.originalLine();
        int column = source.originalColumn();

        // The presence of a space *could* be the "single space" term operator.
        // Or it could just be whitespace around another term operator.
        boolean mightBeSpaceOperator = source.optionallyPresent(Tokens.WHITESPACE);

        // after we've already checked for the single space operator, it's ok to consume comments
        // and surrounding whitespace.
        source.collectComments();

        // see if there is an actual non-space operator
        Optional<OperatorType> type = source.optionalFromEnum(OperatorType.class);

        // if no operator is parsed and we parsed at least one space then we know it's a single space operator
        if (mightBeSpaceOperator && !type.isPresent()) {
            type = Optional.of(OperatorType.SPACE);
        }

        // we didn't parse any operators
        if (!type.isPresent()) return false;

        // skip whitespace now that we know it can't be another space operator.
        source.skipWhitepace();

        // broadcast the parsed operator
        Operator operator = new Operator(line, column, type.get());
        broadcaster.broadcast(operator);
        return true;
    }
}
