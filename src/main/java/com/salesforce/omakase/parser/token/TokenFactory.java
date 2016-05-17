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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.Parser;

/**
 * A factory for retrieving various {@link Token}s. Mainly used by {@link Parser}s.
 * <p>
 * The motivation for using a factory interface for tokens is that it provides the ability for highly-customized input source code
 * grammar. This could be used, for example, to enable newline and whitespace delimited grammar instead of colons, semicolons,
 * brackets, etc...
 * <p>
 * Custom token factories will usually extends from {@link BaseTokenFactory}.
 *
 * @author nmcwilliams
 * @see BaseTokenFactory
 */
public interface TokenFactory {
    /**
     * Gets the {@link Token} representing the end of an at-rule's expression.
     *
     * @return {@link Token} representing the end of an at-rule's expression.
     */
    Token atRuleExpressionEnd();

    /**
     * Gets the {@link Token} designating that the content of an at-rule is terminated (usually a semi-colon).
     *
     * @return {@link Token} designating that the content of an at-rule is terminated.
     */
    Token atRuleTermination();

    /**
     * Gets the {@link Token} representing the opening of an at-rule block.
     *
     * @return {@link Token} representing the opening of an at-rule block.
     */
    Token atRuleBlockBegin();

    /**
     * Gets the {@link Token} representing the closing of an at-rule block.
     *
     * @return {@link Token} representing the closing of an at-rule block.
     */
    Token atRuleBlockEnd();

    /**
     * Gets the {@link Token} representing what the first character of a {@link Selector} must be.
     *
     * @return {@link Token} representing the first character of a {@link Selector}.
     */
    Token selectorBegin();

    /**
     * Gets the {@link Token} representing the delimiter between {@link Selector}s.
     *
     * @return {@link Token} representing the {@link Selector} delimiter.
     */
    Token selectorDelimiter();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Selector}.
     *
     * @return {@link Token} representing the end of the {@link Selector}.
     */
    Token selectorEnd();

    /**
     * Gets the {@link Token} representing the beginning of a declaration block.
     *
     * @return {@link Token} representing the beginning of a declaration block.
     */
    Token declarationBlockBegin();

    /**
     * Gets the {@link Token} representing the end of a declaration block.
     *
     * @return {@link Token} representing the end of a declaration block.
     */
    Token declarationBlockEnd();

    /**
     * Gets the {@link Token} representing the delimiter between {@link Declaration}s.
     *
     * @return The {@link Token} representing the delimiter between {@link Declaration}s.
     */
    Token declarationDelimiter();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Declaration}.
     *
     * @return The {@link Token} representing the end of a {@link Declaration}.
     */
    Token declarationEnd();

    /**
     * Gets the {@link Token} representing what indicates the end of a {@link Declaration}'s property name.
     *
     * @return The {@link Token} representing what indicates the end of a property name.
     */
    Token propertyNameEnd();

    /**
     * Gets the {@link Token} representing an optional special character that may appear before a normal property name in a {@link
     * Declaration}.
     * <p>
     * For example, to enable the star hack the "*" token could be returned. If overriding this method, consider using a {@link
     * CompoundToken} of the super class implementation.
     *
     * @return The {@link Token} representing an optional special {@link Declaration} start character.
     */
    Optional<Token> specialDeclarationBegin();
}
