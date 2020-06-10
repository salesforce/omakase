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

package com.salesforce.omakase.parser.factory;

import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.Tokens;

import java.util.Optional;

import static com.salesforce.omakase.parser.token.Tokens.*;

/**
 * Base class for {@link TokenFactory}s.
 * <p>
 * Subclasses can override methods as appropriate to specify alternative or additional tokens to recognize as various delimiters.
 *
 * @author nmcwilliams
 */
public class BaseTokenFactory implements TokenFactory {
    protected static final Token AT_RULE_BLOCK_BEGIN = OPEN_BRACE;
    protected static final Token AT_RULE_BLOCK_END = CLOSE_BRACE;
    protected static final Token AT_RULE_TERMINATION = SEMICOLON;
    protected static final Token AT_RULE_EXPRESSION_END = AT_RULE_TERMINATION.or(AT_RULE_BLOCK_BEGIN);
    protected static final Token SELECTOR_BEGIN = DOT.or(HASH).or(ALPHA).or(COLON).or(OPEN_BRACKET).or(STAR);
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
        return SELECTOR_BEGIN;
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

    @Override
    public Optional<Token> specialDeclarationBegin() {
        // to allow for the the IE7 star hack - http://en.wikipedia.org/wiki/CSS_filter#Star_hack
        // it's not part of the CSS spec, but it still needs to be handled
        return Optional.of(Tokens.STAR);
    }
}
