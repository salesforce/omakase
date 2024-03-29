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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * Combines two or more {@link Parser}s together. If the first parser does not succeed (i.e., returns false) then subsequent
 * parsers will be tried (until/if one does).
 *
 * @author nmcwilliams
 */
public final class CombinationParser implements Parser {
    private final Parser[] parsers;

    /**
     * Creates a new instance using the given {@link Parser}s, in order.
     *
     * @param parsers
     *     The parsers.
     */
    public CombinationParser(Parser... parsers) {
        this.parsers = parsers;
    }

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        for (Parser parser : parsers) {
            if (parser.parse(source, grammar, broadcaster)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster, boolean parentIsConditional) {
        for (Parser parser : parsers) {
            if (parser.parse(source, grammar, broadcaster, parentIsConditional)) {
                return true;
            }
        }
        return false;
    }
}
