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

import com.salesforce.omakase.parser.Parser;

import static com.salesforce.omakase.parser.token.Tokens.*;

/**
 * A {@link TokenFactory} for retrieving standard {@link Token} objects. Mainly using by {@link Parser}s.
 *
 * @author nmcwilliams
 */
public class StandardTokenFactory implements TokenFactory {
    private static final Token AT_RULE_BLOCK_BEGIN = OPEN_BRACE;
    private static final Token AT_RULE_BLOCK_END = CLOSE_BRACE;
    private static final Token AT_RULE_TERMINATION = SEMICOLON;
    private static final Token AT_RULE_EXPRESSION_END = AT_RULE_TERMINATION.or(AT_RULE_BLOCK_BEGIN);
    private static final Token SELETOR_BEGIN = DOT.or(HASH).or(ALPHA).or(COLON).or(STAR);
    private static final Token SELECTOR_DELIMITER = COMMA;
    private static final Token SELECTOR_END = SELECTOR_DELIMITER.or(OPEN_BRACE);
    private static final Token PROPERTY_START = ALPHA.or(HYPHEN);
    private static final Token DECLARATION_END = SEMICOLON.or(CLOSE_BRACE);

    private static final TokenFactory instance = new StandardTokenFactory();

    /** Only here to allow for subclassing. Clients should use {@link #instance()} instead. */
    protected StandardTokenFactory() {}

    /**
     * Gets the cached factory instance.
     *
     * @return The cached instance.
     */
    public static TokenFactory instance() {
        return instance;
    }

    @Override
    public Token atRuleExpressionEnd() {
        return AT_RULE_EXPRESSION_END;
    }

    @Override
    public Token atRuleTermination() {
        return AT_RULE_TERMINATION;
    }

    @Override
    public Token atRuleBlockBegin() {
        return AT_RULE_BLOCK_BEGIN;
    }

    @Override
    public Token atRuleBlockEnd() {
        return AT_RULE_BLOCK_END;
    }

    @Override
    public Token selectorBegin() {
        return SELETOR_BEGIN;
    }

    @Override
    public Token selectorDelimiter() {
        return SELECTOR_DELIMITER;
    }

    @Override
    public Token selectorEnd() {
        return SELECTOR_END;
    }

    @Override
    public Token declarationBlockBegin() {
        return OPEN_BRACE;
    }

    @Override
    public Token declarationBlockEnd() {
        return CLOSE_BRACE;
    }

    @Override
    public Token declarationBegin() {
        return PROPERTY_START;
    }

    @Override
    public Token declarationDelimiter() {
        return SEMICOLON;
    }

    @Override
    public Token declarationEnd() {
        return DECLARATION_END;
    }

    @Override
    public Token propertyNameEnd() {
        return COLON;
    }
}
