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

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.parser.Parser;

/**
 * A factory for retrieving various {@link Token}s. Mainly used by {@link Parser}s.
 * <p/>
 * The motivation for using a factory interface for tokens is that it provides the ability for highly-customized input source code
 * grammar. This could be used, for example, to enable grammar similar to the popular Stylus open source library.
 *
 * @author nmcwilliams
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
     * Gets the {@link Token} representing what the first character of a {@link Declaration} must be (property name).
     *
     * @return {@link Token} representing the first character of a {@link Declaration}.
     */
    Token declarationBegin();

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
}
