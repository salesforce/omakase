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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.parser.atrule.KeyframeRuleParser;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorParser;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorSequenceParser;
import com.salesforce.omakase.parser.atrule.MediaQueryExpressionParser;
import com.salesforce.omakase.parser.atrule.MediaQueryListParser;
import com.salesforce.omakase.parser.atrule.MediaQueryParser;
import com.salesforce.omakase.parser.declaration.*;
import com.salesforce.omakase.parser.raw.RawAtRuleParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.parser.raw.RawDeclarationSequenceParser;
import com.salesforce.omakase.parser.raw.RawRuleParser;
import com.salesforce.omakase.parser.raw.RawSelectorParser;
import com.salesforce.omakase.parser.raw.RawSelectorSequenceParser;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.parser.selector.*;

/**
 * A cache of {@link Parser} instances.
 * <p/>
 * Each {@link Parser} that is used should usually only be created once (enabled by this class). This is tenable due to the fact
 * that {@link Parser}s are not allowed to maintain state.
 *
 * @author nmcwilliams
 */
public final class ParserFactory {
    /** do not construct */
    private ParserFactory() {}

    /* generic parsers */
    private static final Parser stylesheet = new StylesheetParser();

    private static final Parser atRule = new RawAtRuleParser();
    private static final Parser rule = new RawRuleParser();
    private static final Parser statement = rule.or(atRule);

    private static final Parser selector = new RawSelectorParser();
    private static final Parser selectorSequence = new RawSelectorSequenceParser();

    private static final Parser declaration = new RawDeclarationParser();
    private static final Parser declarationSequence = new RawDeclarationSequenceParser();

    /* refined selectors */
    private static final Parser complexSelector = new ComplexSelectorParser();

    private static final Parser combinator = new CombinatorParser();
    private static final Parser classSelector = new ClassSelectorParser();
    private static final Parser idSelector = new IdSelectorParser();
    private static final Parser attributeSelector = new AttributeSelectorParser();
    private static final Parser typeSelector = new TypeSelectorParser();
    private static final Parser universalSelector = new UniversalSelectorParser();
    private static final Parser pseudoSelector = new PseudoSelectorParser();
    private static final Parser typeOrUniversal = typeSelector.or(universalSelector);

    private static final Parser repeatableSelector = classSelector
        .or(idSelector)
        .or(attributeSelector)
        .or(pseudoSelector);

    /* refined declaration values */
    private static final Parser numericalValue = new NumericalValueParser();
    private static final Parser functionValue = new FunctionValueParser();
    private static final Parser keywordValue = new KeywordValueParser();
    private static final Parser hexColorValue = new HexColorValueParser();
    private static final Parser stringValue = new StringValueParser();
    private static final Parser unicodeRangeValue = new UnicodeRangeValueParser();

    private static final Parser term = hexColorValue
        .or(functionValue)
        .or(unicodeRangeValue)
        .or(keywordValue)
        .or(numericalValue)
        .or(stringValue);

    private static final Parser termSequence = new TermSequenceParser();
    private static final Parser operator = new OperatorParser();
    private static final Parser important = new ImportantParser();
    private static final Parser propertyValue = new PropertyValueParser();

    /* media queries */
    private static final Parser mediaQueryList = new MediaQueryListParser();
    private static final Parser mediaQuery = new MediaQueryParser();
    private static final Parser mediaQueryExpression = new MediaQueryExpressionParser();

    /* keyframes */
    private static final Parser keyframeSelector = new KeyframeSelectorParser();
    private static final Parser keyframeSelectorSequence = new KeyframeSelectorSequenceParser();
    private static final Parser keyframeRule = new KeyframeRuleParser();

    /**
     * Gets the {@link StylesheetParser}.
     *
     * @return The parser instance.
     */
    public static Parser stylesheetParser() {
        return stylesheet;
    }

    /**
     * Gets the {@link RawAtRuleParser}.
     *
     * @return The parser instance.
     */
    public static Parser atRuleParser() {
        return atRule;
    }

    /**
     * Gets the {@link RawRuleParser}.
     *
     * @return The parser instance.
     */
    public static Parser ruleParser() {
        return rule;
    }

    /**
     * Gets a parser to parse a single statement (rule or at-rule).
     *
     * @return The parser instance.
     */
    public static Parser statementParser() {
        return statement;
    }

    /**
     * Gets the {@link RawSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser rawSelectorParser() {
        return selector;
    }

    /**
     * Gets the {@link RawSelectorSequenceParser}.
     *
     * @return The parser instance.
     */
    public static Parser rawSelectorSequenceParser() {
        return selectorSequence;
    }

    /**
     * Gets the {@link RawDeclarationParser}.
     *
     * @return The parser instance.
     */
    public static Parser rawDeclarationParser() {
        return declaration;
    }

    /**
     * Gets the {@link RawDeclarationSequenceParser}.
     *
     * @return The parser instance.
     */
    public static Parser rawDeclarationSequenceParser() {
        return declarationSequence;
    }

    /**
     * Gets the {@link ComplexSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser complexSelectorParser() {
        return complexSelector;
    }

    /**
     * Gets the {@link CombinatorParser}.
     *
     * @return The parser instance.
     */
    public static Parser combinatorParser() {
        return combinator;
    }

    /**
     * Gets the {@link ClassSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser classSelectorParser() {
        return classSelector;
    }

    /**
     * Gets the {@link IdSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser idSelectorParser() {
        return idSelector;
    }

    /**
     * Gets the {@link AttributeSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser attributeSelectorParser() {
        return attributeSelector;
    }

    /**
     * Gets the {@link TypeSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser typeSelectorParser() {
        return typeSelector;
    }

    /**
     * Gets the {@link UniversalSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser universalSelectorParser() {
        return universalSelector;
    }

    /**
     * Gets the {@link PseudoSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser pseudoSelectorParser() {
        return pseudoSelector;
    }

    /**
     * Gets the parser to parse {@link SimpleSelector} (excluding type and universal selectors) or a {@link
     * PseudoElementSelector}.
     *
     * @return The parser instance.
     */
    public static Parser repeatableSelector() {
        return repeatableSelector;
    }

    /**
     * Gets the parser to parse a {@link TypeSelector} or a {@link UniversalSelectorParser}.
     *
     * @return The parser instance.
     */
    public static Parser typeOrUniversaleSelectorParser() {
        return typeOrUniversal;
    }

    /**
     * Gets the {@link NumericalValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser numericalValueParser() {
        return numericalValue;
    }

    /**
     * Gets the {@link FunctionValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser functionValueParser() {
        return functionValue;
    }

    /**
     * Gets the {@link KeywordValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser keywordValueParser() {
        return keywordValue;
    }

    /**
     * Gets the {@link HexColorValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser hexColorValueParser() {
        return hexColorValue;
    }

    /**
     * Gets the {@link StringValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser stringValueParser() {
        return stringValue;
    }

    /**
     * Gets the {@link UnicodeRangeValueParser}.
     *
     * @return The parser instance.
     */
    public static Parser unicodeRangeValueParser() {
        return unicodeRangeValue;
    }

    /**
     * Gets the {@link OperatorParser}.
     *
     * @return The parser instance.
     */
    public static Parser operatorParser() {
        return operator;
    }

    /**
     * Gets the parser to parse a "important!" value.
     *
     * @return The parser instance.
     */
    public static Parser importantParser() {
        return important;
    }

    /**
     * Gets the parser to parse a {@link Term} value.
     * <p/>
     * This differs from the other term parsers in that it parses just a single {@link Term}.
     *
     * @return The parser instance.
     */
    public static Parser termParser() {
        return term;
    }

    /**
     * Gets the {@link TermSequenceParser}.
     * <p/>
     * This differs from the other term parsers in that it parses a list of both {@link Term}s AND {@link Operator}s, but it does
     * not parse importants or broadcast a {@link PropertyValue}.
     *
     * @return The parser instance.
     */
    public static Parser termSequenceParser() {
        return termSequence;
    }

    /**
     * Gets the {@link PropertyValueParser}.
     * <p/>
     * This differs from the other term parsers in that it parses a list of both {@link Term}s AND {@link Operator}s, plus it
     * parses importants and broadcasts a {@link PropertyValue}.
     *
     * @return The parser instance.
     */
    public static Parser propertyValueParser() {
        return propertyValue;
    }

    /**
     * Gets the {@link MediaQueryListParser}.
     *
     * @return The parser instance.
     */
    public static Parser mediaQueryListParser() {
        return mediaQueryList;
    }

    /**
     * Gets the {@link MediaQueryParser}.
     *
     * @return The parser instance.
     */
    public static Parser mediaQueryParser() {
        return mediaQuery;
    }

    /**
     * Gets the {@link MediaQueryExpressionParser}.
     *
     * @return The parser instance.
     */
    public static Parser mediaExpressionParser() {
        return mediaQueryExpression;
    }

    /**
     * Gets the parser to parse a {@link KeyframeSelector}.
     *
     * @return The parser instance.
     */
    public static Parser keyframeSelectorParser() {
        return keyframeSelector;
    }

    /**
     * Gets the {@link KeyframeSelectorSequenceParser}.
     *
     * @return The parser instance.
     */
    public static Parser keyframeSelectorSequenceParser() {
        return keyframeSelectorSequence;
    }

    /**
     * Gets the {@link KeyframeRuleParser}.
     *
     * @return The parser instance.
     */
    public static Parser keyframeRuleParser() {
        return keyframeRule;
    }
}
