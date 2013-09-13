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

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Enum of the types of CSS combinators.
 *
 * @author nmcwilliams
 */
public enum CombinatorType implements TokenEnum<CombinatorType> {
    // ordered by likelihood of occurrence

    /** descendant combinator */
    DESCENDANT(Tokens.NEVER_MATCH),

    /** child combinator */
    CHILD(Tokens.GREATER_THAN),

    /** adjacent sibling combinator */
    ADJACENT_SIBLING(Tokens.PLUS),

    /** general sibling combinator */
    GENERAL_SIBLING(Tokens.TILDE);

    private final Token token;

    CombinatorType(Token token) {
        this.token = token;
    }

    @Override
    public Token token() {
        return token;
    }
}
