/*
 * Copyright (c) 2016, salesforce.com, inc.
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

import com.salesforce.omakase.parser.factory.ParserFactory;
import com.salesforce.omakase.parser.factory.StandardParserFactory;
import com.salesforce.omakase.parser.factory.StandardTokenFactory;
import com.salesforce.omakase.parser.factory.TokenFactory;

/**
 * Contains factories for grammar providers.
 * <p>
 * Use instances of this class to obtain other parsers or tokens during parsing and refinement.
 *
 * @author nmcwilliams
 * @see TokenFactory
 * @see ParserFactory
 */
public final class Grammar {
    private final TokenFactory tokenFactory;
    private final ParserFactory parserFactory;

    /**
     * Creates a new instance using standard grammar constructs.
     */
    public Grammar() {
        this(null, null);
    }

    /**
     * Creates a new instance using standard or custom grammar constructs.
     *
     * @param tokenFactory
     *     The token factory, or null to use the standard.
     * @param parserFactory
     *     The parser factory, or null to use the standard.
     */
    public Grammar(TokenFactory tokenFactory, ParserFactory parserFactory) {
        this.tokenFactory = tokenFactory != null ? tokenFactory : StandardTokenFactory.instance();
        this.parserFactory = parserFactory != null ? parserFactory : StandardParserFactory.instance();
    }

    /**
     * Gets the {@link TokenFactory}.
     *
     * @return The {@link TokenFactory}.
     */
    public TokenFactory token() {
        return tokenFactory;
    }

    /**
     * Gets the {@link ParserFactory}.
     *
     * @return The {@link ParserFactory}.
     */
    public ParserFactory parser() {
        return parserFactory;
    }
}
