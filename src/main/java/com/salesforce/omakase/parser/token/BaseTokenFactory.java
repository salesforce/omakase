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

package com.salesforce.omakase.parser.token;

import static com.salesforce.omakase.parser.token.Tokens.*;

/**
 * Base class for {@link TokenFactory}s.
 * <p/>
 * Subclasses can override methods as appropriate to specify alternative or additional tokens to recognize as various delimiters.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("JavaDoc")
public class BaseTokenFactory implements TokenFactory {
    protected static final Token AT_RULE_BLOCK_BEGIN = OPEN_BRACE;
    protected static final Token AT_RULE_BLOCK_END = CLOSE_BRACE;
    protected static final Token AT_RULE_TERMINATION = SEMICOLON;
    protected static final Token AT_RULE_EXPRESSION_END = AT_RULE_TERMINATION.or(AT_RULE_BLOCK_BEGIN);
    protected static final Token SELETOR_BEGIN = DOT.or(HASH).or(ALPHA).or(COLON).or(STAR);
    protected static final Token SELECTOR_DELIMITER = COMMA;
    protected static final Token SELECTOR_END = SELECTOR_DELIMITER.or(OPEN_BRACE);
    protected static final Token DECLARATION_END = SEMICOLON.or(CLOSE_BRACE);

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
