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

import java.util.Optional;

import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Source;

/**
 * Parses a single term {@link Operator}.
 *
 * @author nmcwilliams
 * @see Operator
 */
public final class OperatorParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        // move past comments and whitespace
        source.collectComments();

        // save off the current position before parsing any content
        int line = source.originalLine();
        int column = source.originalColumn();

        // see if there is an actual non-space operator
        Optional<OperatorType> type = source.optionalFromEnum(OperatorType.class);

        // we didn't parse any operators
        if (!type.isPresent()) return false;

        // broadcast the parsed operator
        Operator operator = new Operator(line, column, type.get());
        broadcaster.broadcast(operator);
        return true;
    }

}
