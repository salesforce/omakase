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

package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.plugin.GrammarPlugin;

/**
 * Mainly for use with custom {@link GrammarPlugin}s that need to specify additional characters not already covered by
 * {@link Tokens}.
 *
 * @author nmcwilliams
 */
public final class SimpleToken implements Token {
    private final char token;
    private final String description;

    /**
     * Creates a new instance matching the given character.
     *
     * @param token
     *     The character.
     */
    public SimpleToken(char token) {
        this(token, String.valueOf(token));
    }

    /**
     * Creates a new instance matching the given character and with the given description.
     *
     * @param token
     *     The character.
     * @param description
     *     A description of the token for use in certain error messages. See examples in {@link Tokens}.
     */
    public SimpleToken(char token, String description) {
        this.token = token;
        this.description = description;
    }

    @Override
    public boolean matches(char c) {
        return (token - c) == 0;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }
}
