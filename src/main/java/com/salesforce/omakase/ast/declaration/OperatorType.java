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

/**

 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenEnum;
import com.salesforce.omakase.parser.token.Tokens;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.Writable;

import java.io.IOException;

/**
 * An operator, or separator, between {@link Term}s in a {@link PropertyValue}.
 *
 * @author nmcwilliams
 */
public enum OperatorType implements TokenEnum, Writable {
    /** comma separator */
    COMMA(Tokens.COMMA, ','),
    /** slash separator */
    SLASH(Tokens.FORWARD_SLASH, '/'),
    /** white space separator */
    SPACE(Tokens.WHITESPACE, ' ');

    private final Token token;
    private final char symbol;

    OperatorType(Token token, char symbol) {
        this.token = token;
        this.symbol = symbol;
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(symbol);
    }
}
