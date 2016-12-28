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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;

/**
 * Parses a sequence of both {@link Term}s and {@link Operator}s.
 * <p>
 * This does not parts importants or broadcast a {@link PropertyValue}.
 *
 * @author nmcwilliams
 * @see PropertyValueParser
 */
public final class TermSequenceParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        // gather comments and skip whitespace
        source.collectComments();

        Parser term = grammar.parser().termParser();
        Parser operator = grammar.parser().operatorParser();

        // parse the first term
        boolean foundTerm = term.parse(source, grammar, broadcaster);
        if (!foundTerm) return false;

        while (foundTerm) {
            boolean foundWhitespace = source.findComments(true); // term parsers must not skip whitespace at the end
            boolean foundOperator = operator.parse(source, grammar, broadcaster);

            if (foundWhitespace || foundOperator) {
                foundTerm = term.parse(source, grammar, broadcaster);
            } else {
                foundTerm = false;
            }

            if (foundOperator && !foundTerm) {
                throw new ParserException(source, Message.TRAILING_OPERATOR);
            }
        }

        return true;
    }

}
