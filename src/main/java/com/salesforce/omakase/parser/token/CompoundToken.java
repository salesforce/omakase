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

package com.salesforce.omakase.parser.token;

import com.salesforce.omakase.util.As;
import com.salesforce.omakase.parser.Source;

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
