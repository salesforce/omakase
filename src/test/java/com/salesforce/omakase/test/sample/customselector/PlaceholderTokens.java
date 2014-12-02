/*
 * Copyright (C) 2014 salesforce.com, inc.
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

package com.salesforce.omakase.test.sample.customselector;

import com.salesforce.omakase.parser.token.CompoundToken;
import com.salesforce.omakase.parser.token.Token;

/**
* TODO description
*
* @author nmcwilliams
*/
public enum PlaceholderTokens implements Token {
    PERCENTAGE('%'),
    PIPE('|');

    private final char symbol;

    PlaceholderTokens(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean matches(char c) {
        return (symbol - c) == 0;
    }

    @Override
    public String description() {
        return "" + symbol;
    }

    @Override
    public Token or(Token other) {
        return new CompoundToken(this, other);
    }
}
