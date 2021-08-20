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

import com.salesforce.omakase.parser.Parser;

/**
 * A factory for retrieving various {@link Parser}s.
 * <p>
 * High-level parsers and refiners can use this to retrieve appropriate parsers for sub units.
 * <p>
 * Implementations can dictate a highly-customized structure that allows for changes and updates to how parsing of various units
 * is performed.
 * <p>
 * Custom parser factories will usually extends from {@link BaseTokenFactory}.
 *
 * @author nmcwilliams
 */
public interface ParserFactory {
    /**
     * Gets the {@link StylesheetParser}.
     *
     * @return The parser instance.
     */
    Parser stylesheetParser();

    /**
     * Gets the {@link AtRuleParser}.
     *
     * @return The parser instance.
     */
    Parser atRuleParser();

    /**
     * Gets the {@link RuleParser}.
     *
     * @return The parser instance.
     */
    Parser ruleParser();

    /**
     * Gets a parser to parse a single statement (rule or at-rule).
     *
     * @return The parser instance.
     */
    Parser statementParser();

    /**
     * Gets the {@link SelectorParser}.
     *
     * @return The parser instance.
     */
    Parser rawSelectorParser();

    /**
     * Gets the {@link SelectorSequenceParser}.
     *
     * @return The parser instance.
     */
    Parser rawSelectorSequenceParser();

    /**
     * Gets the {@link DeclarationParser}.
     *
     * @return The parser instance.
     */
    Parser rawDeclarationParser();

    /**
     * Gets the {@link DeclarationSequenceParser}.
     *
     * @return The parser instance.
     */
    Parser rawDeclarationSequenceParser();

    /**
     * Gets the {@link ComplexSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser complexSelectorParser();

    /**
     * Gets the {@link CombinatorParser}.
     *
     * @return The parser instance.
     */
    Parser combinatorParser();

    /**
     * Gets the {@link ClassSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser classSelectorParser();

    /**
     * Gets the {@link IdSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser idSelectorParser();

    /**
     * Gets the {@link AttributeSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser attributeSelectorParser();

    /**
     * Gets the {@link TypeSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser typeSelectorParser();

    /**
     * Gets the {@link UniversalSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser universalSelectorParser();

    /**
     * Gets the {@link PseudoSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser pseudoSelectorParser();

    /**
     * Gets the parser to parse {@link SimpleSelector} (excluding type and universal selectors) or a {@link
     * PseudoElementSelector}.
     *
     * @return The parser instance.
     */
    Parser repeatableSelector();

    /**
     * Gets the parser to parse a {@link TypeSelector} or a {@link UniversalSelectorParser}.
     *
     * @return The parser instance.
     */
    Parser typeOrUniversaleSelectorParser();

    /**
     * Gets the {@link NumericalValueParser}.
     *
     * @return The parser instance.
     */
    Parser numericalValueParser();

    /**
     * Gets the {@link FunctionValueParser}.
     *
     * @return The parser instance.
     */
    Parser functionValueParser();

    /**
     * Gets the {@link KeywordValueParser}.
     *
     * @return The parser instance.
     */
    Parser keywordValueParser();

    /**
     * Gets the {@link HexColorValueParser}.
     *
     * @return The parser instance.
     */
    Parser hexColorValueParser();

    /**
     * Gets the {@link StringValueParser}.
     *
     * @return The parser instance.
     */
    Parser stringValueParser();

    /**
     * Gets the {@link UnicodeRangeValueParser}.
     *
     * @return The parser instance.
     */
    Parser unicodeRangeValueParser();

    /**
     * Gets the {@link OperatorParser}.
     *
     * @return The parser instance.
     */
    Parser operatorParser();

    /**
     * Gets the parser to parse a "important!" value.
     *
     * @return The parser instance.
     */
    Parser importantParser();

    /**
     * Gets the parser to parse a {@link Term} value.
     * <p>
     * This differs from the other term parsers in that it parses just a single {@link Term}.
     *
     * @return The parser instance.
     */
    Parser termParser();

    /**
     * Gets the {@link TermSequenceParser}.
     * <p>
     * This differs from the other term parsers in that it parses a list of both {@link Term}s AND {@link Operator}s, but it does
     * not parse importants or broadcast a {@link PropertyValue}.
     *
     * @return The parser instance.
     */
    Parser termSequenceParser();

    /**
     * Gets the {@link PropertyValueParser}.
     * <p>
     * This differs from the other term parsers in that it parses a list of both {@link Term}s AND {@link Operator}s, plus it
     * parses importants and broadcasts a {@link PropertyValue}.
     *
     * @return The parser instance.
     */
    Parser propertyValueParser();

    /**
     * Gets the {@link MediaQueryListParser}.
     *
     * @return The parser instance.
     */
    Parser mediaQueryListParser();

    /**
     * Gets the {@link MediaQueryParser}.
     *
     * @return The parser instance.
     */
    Parser mediaQueryParser();

    /**
     * Gets the {@link MediaQueryExpressionParser}.
     *
     * @return The parser instance.
     */
    Parser mediaExpressionParser();

    /**
     * Gets the parser to parse a {@link KeyframeSelector}.
     *
     * @return The parser instance.
     */
    Parser keyframeSelectorParser();

    /**
     * Gets the {@link KeyframeSelectorSequenceParser}.
     *
     * @return The parser instance.
     */
    Parser keyframeSelectorSequenceParser();

    /**
     * Gets the {@link KeyframeRuleParser}.
     *
     * @return The parser instance.
     */
    Parser keyframeRuleParser();
}
