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

import com.google.common.base.CharMatcher;

/**
 * An object wrapper over {@link CharMatcher}s.
 *
 * @author nmcwilliams
 */
public interface Token {
    /**
     * Whether the given character matchers this {@link Token}.
     *
     * @param c
     *     Compare to this character.
     *
     * @return true if this token matches the given character.
     */
    boolean matches(char c);

    /**
     * Gets a description of the token. This is used in error-reporting to indicate what was expected.
     *
     * @return The description.
     */
    String description();

    /**
     * A Utility to create a new {@link CompoundToken}, combining this {@link Token} with another one. This is useful for OR
     * character comparisons.
     *
     * @param other
     *     The other {@link Token}.
     *
     * @return A {@link CompoundToken} instance.
     */
    Token or(Token other);
}
