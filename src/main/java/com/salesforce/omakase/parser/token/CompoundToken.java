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

package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.util.As;

/**
 * A combination matcher that does an OR comparison of two {@link Token}s.
 *
 * @author nmcwilliams
 */
public final class CompoundToken implements Token {
    private final String description;
    private final Token first;
    private final Token second;

    /**
     * Constructs a new {@link CompoundToken} for doing OR character comparisons. The descriptions of each will be combined.
     *
     * @param first
     *     The first {@link Token}.
     * @param second
     *     The second {@link Token}.
     */
    public CompoundToken(Token first, Token second) {
        this.first = first;
        this.second = second;
        this.description = first.description() + " OR " + second.description();
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean matches(char c) {
        if (c == Source.NULL_CHAR) return false;
        return first.matches(c) || second.matches(c);
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }

    @Override
    public String toString() {
        return As.string(this).add("description", description).toString();
    }
}
