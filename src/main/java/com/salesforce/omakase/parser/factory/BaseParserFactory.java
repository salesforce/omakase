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

import com.salesforce.omakase.parser.CombinationParser;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.RuleParser;
import com.salesforce.omakase.parser.StylesheetParser;
import com.salesforce.omakase.parser.atrule.AtRuleParser;
import com.salesforce.omakase.parser.atrule.KeyframeRuleParser;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorParser;
import com.salesforce.omakase.parser.atrule.KeyframeSelectorSequenceParser;
import com.salesforce.omakase.parser.atrule.MediaQueryExpressionParser;
import com.salesforce.omakase.parser.atrule.MediaQueryListParser;
import com.salesforce.omakase.parser.atrule.MediaQueryParser;
import com.salesforce.omakase.parser.declaration.DeclarationParser;
import com.salesforce.omakase.parser.declaration.DeclarationSequenceParser;
import com.salesforce.omakase.parser.declaration.FunctionValueParser;
import com.salesforce.omakase.parser.declaration.HexColorValueParser;
import com.salesforce.omakase.parser.declaration.ImportantParser;
import com.salesforce.omakase.parser.declaration.KeywordValueParser;
import com.salesforce.omakase.parser.declaration.NumericalValueParser;
import com.salesforce.omakase.parser.declaration.OperatorParser;
import com.salesforce.omakase.parser.declaration.PropertyValueParser;
import com.salesforce.omakase.parser.declaration.StringValueParser;
import com.salesforce.omakase.parser.declaration.TermSequenceParser;
import com.salesforce.omakase.parser.declaration.UnicodeRangeValueParser;
import com.salesforce.omakase.parser.selector.AttributeSelectorParser;
import com.salesforce.omakase.parser.selector.ClassSelectorParser;
import com.salesforce.omakase.parser.selector.CombinatorParser;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.parser.selector.IdSelectorParser;
import com.salesforce.omakase.parser.selector.PseudoSelectorParser;
import com.salesforce.omakase.parser.selector.SelectorParser;
import com.salesforce.omakase.parser.selector.SelectorSequenceParser;
import com.salesforce.omakase.parser.selector.TypeSelectorParser;
import com.salesforce.omakase.parser.selector.UniversalSelectorParser;

/**
 * Base class for {@link ParserFactory}s.
 * <p>
 * Subclasses can override methods as appropriate to specify alternative parser implementations.
 *
 * @author nmcwilliams
 */
public class BaseParserFactory implements ParserFactory {
    /* generic parsers */
    private final Parser stylesheet = new StylesheetParser();

    private final Parser atRule = new AtRuleParser();
    private final Parser rule = new RuleParser();
    private final Parser statement = new CombinationParser(rule, atRule);

    private final Parser selector = new SelectorParser();
    private final Parser selectorSequence = new SelectorSequenceParser();

    private final Parser declaration = new DeclarationParser();
    private final Parser declarationSequence = new DeclarationSequenceParser();

    /* refined selectors */
    private final Parser complexSelector = new ComplexSelectorParser();

    private final Parser combinator = new CombinatorParser();
    private final Parser classSelector = new ClassSelectorParser();
    private final Parser idSelector = new IdSelectorParser();
    private final Parser attributeSelector = new AttributeSelectorParser();
    private final Parser typeSelector = new TypeSelectorParser();
    private final Parser universalSelector = new UniversalSelectorParser();
    private final Parser pseudoSelector = new PseudoSelectorParser();
    private final Parser typeOrUniversal = new CombinationParser(typeSelector, universalSelector);

    private final Parser repeatableSelector = new CombinationParser(
        classSelector, idSelector, attributeSelector, pseudoSelector);

    /* refined declaration values */
    private final Parser numericalValue = new NumericalValueParser();
    private final Parser functionValue = new FunctionValueParser();
    private final Parser keywordValue = new KeywordValueParser();
    private final Parser hexColorValue = new HexColorValueParser();
    private final Parser stringValue = new StringValueParser();
    private final Parser unicodeRangeValue = new UnicodeRangeValueParser();

    private final Parser term = new CombinationParser(
        hexColorValue, functionValue, unicodeRangeValue, keywordValue, numericalValue, stringValue);

    private final Parser termSequence = new TermSequenceParser();
    private final Parser operator = new OperatorParser();
    private final Parser important = new ImportantParser();
    private final Parser propertyValue = new PropertyValueParser();

    /* media queries */
    private final Parser mediaQueryList = new MediaQueryListParser();
    private final Parser mediaQuery = new MediaQueryParser();
    private final Parser mediaQueryExpression = new MediaQueryExpressionParser();

    /* keyframes */
    private final Parser keyframeSelector = new KeyframeSelectorParser();
    private final Parser keyframeSelectorSequence = new KeyframeSelectorSequenceParser();
    private final Parser keyframeRule = new KeyframeRuleParser();

    @Override
    public Parser stylesheetParser() {
        return stylesheet;
    }

    @Override
    public Parser atRuleParser() {
        return atRule;
    }

    @Override
    public Parser ruleParser() {
        return rule;
    }

    @Override
    public Parser statementParser() {
        return statement;
    }

    @Override
    public Parser rawSelectorParser() {
        return selector;
    }

    @Override
    public Parser rawSelectorSequenceParser() {
        return selectorSequence;
    }

    @Override
    public Parser rawDeclarationParser() {
        return declaration;
    }

    @Override
    public Parser rawDeclarationSequenceParser() {
        return declarationSequence;
    }

    @Override
    public Parser complexSelectorParser() {
        return complexSelector;
    }

    @Override
    public Parser combinatorParser() {
        return combinator;
    }

    @Override
    public Parser classSelectorParser() {
        return classSelector;
    }

    @Override
    public Parser idSelectorParser() {
        return idSelector;
    }

    @Override
    public Parser attributeSelectorParser() {
        return attributeSelector;
    }

    @Override
    public Parser typeSelectorParser() {
        return typeSelector;
    }

    @Override
    public Parser universalSelectorParser() {
        return universalSelector;
    }

    @Override
    public Parser pseudoSelectorParser() {
        return pseudoSelector;
    }

    @Override
    public Parser repeatableSelector() {
        return repeatableSelector;
    }

    @Override
    public Parser typeOrUniversaleSelectorParser() {
        return typeOrUniversal;
    }

    @Override
    public Parser numericalValueParser() {
        return numericalValue;
    }

    @Override
    public Parser functionValueParser() {
        return functionValue;
    }

    @Override
    public Parser keywordValueParser() {
        return keywordValue;
    }

    @Override
    public Parser hexColorValueParser() {
        return hexColorValue;
    }

    @Override
    public Parser stringValueParser() {
        return stringValue;
    }

    @Override
    public Parser unicodeRangeValueParser() {
        return unicodeRangeValue;
    }

    @Override
    public Parser operatorParser() {
        return operator;
    }

    @Override
    public Parser importantParser() {
        return important;
    }

    @Override
    public Parser termParser() {
        return term;
    }

    @Override
    public Parser termSequenceParser() {
        return termSequence;
    }

    @Override
    public Parser propertyValueParser() {
        return propertyValue;
    }

    @Override
    public Parser mediaQueryListParser() {
        return mediaQueryList;
    }

    @Override
    public Parser mediaQueryParser() {
        return mediaQuery;
    }

    @Override
    public Parser mediaExpressionParser() {
        return mediaQueryExpression;
    }

    @Override
    public Parser keyframeSelectorParser() {
        return keyframeSelector;
    }

    @Override
    public Parser keyframeSelectorSequenceParser() {
        return keyframeSelectorSequence;
    }

    @Override
    public Parser keyframeRuleParser() {
        return keyframeRule;
    }
}
