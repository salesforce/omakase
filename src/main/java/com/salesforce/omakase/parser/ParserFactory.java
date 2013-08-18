/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.declaration.value.Term;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.parser.declaration.*;
import com.salesforce.omakase.parser.raw.*;
import com.salesforce.omakase.parser.selector.*;

/**
 * A cache of {@link Parser} instances.
 * 
 * <p>
 * Each {@link Parser} that is used should usually only be created once (enabled by this class). This is tenable due to
 * the fact that {@link Parser}s are not allowed to maintain state.
 * 
 * @author nmcwilliams
 */
public final class ParserFactory {
    /* parsers */
    private static final Parser stylesheet = new StylesheetParser();
    private static final Parser atRule = new AtRuleParser();
    private static final Parser rule = new RuleParser();
    private static final Parser statement = atRule.or(rule);
    private static final Parser rawDeclaration = new RawDeclarationParser();
    private static final Parser selectorGroup = new SelectorGroupParser();
    private static final Parser rawSelector = new RawSelectorParser();
    private static final Parser refinedSelector = new RefinedSelectorParser();

    private static final Parser combinator = new CombinatorParser();
    private static final Parser classSelector = new ClassSelectorParser();
    private static final Parser idSelector = new IdSelectorParser();
    private static final Parser attributeSelector = new AttributeSelectorParser();
    private static final Parser typeSelector = new TypeSelectorParser();
    private static final Parser universalSelector = new UniversalSelectorParser();
    private static final Parser pseudoClassSelector = new PseudoClassSelectorParser();
    private static final Parser pseudoElementSelector = new PseudoElementSelectorParser();
    private static final Parser negationSelector = new NegationSelectorParser();

    private static final Parser simpleSelectorSequence = new SimpleSelectorSequenceParser();
    private static final Parser simpleSelectorStart = typeSelector.or(universalSelector);
    private static final Parser simpleSelector = idSelector.or(classSelector)
        .or(attributeSelector).or(pseudoClassSelector).or(negationSelector);

    private static final Parser termList = new TermListParser();

    private static final Parser numericalValue = new NumericalValueParser();
    private static final Parser functionValue = new FunctionValueParser();
    private static final Parser keywordValue = new KeywordValueParser();
    private static final Parser hexColorValue = new HexColorValueParser();
    private static final Parser stringValue = new StringValueParser();

    private static final Parser term = numericalValue.or(functionValue).or(keywordValue).or(hexColorValue).or(stringValue);

    /**
     * Gets the {@link StylesheetParser}.
     * 
     * @return The parser instance.
     */
    public static Parser stylesheetParser() {
        return stylesheet;
    }

    /**
     * Gets the statement parser.
     * 
     * @return The parser instance.
     */
    public static Parser statementParser() {
        return statement;
    }

    /**
     * Gets the {@link AtRuleParser}.
     * 
     * @return The parser instance.
     */
    public static Parser atRuleParser() {
        return atRule;
    }

    /**
     * Gets the {@link RuleParser}.
     * 
     * @return The parser instance.
     */
    public static Parser ruleParser() {
        return rule;
    }

    /**
     * Gets the {@link SelectorGroupParser}.
     * 
     * @return The parser instance.
     */
    public static Parser selectorGroupParser() {
        return selectorGroup;
    }

    /**
     * Gets the {@link RawSelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser rawSelectorParser() {
        return rawSelector;
    }

    /**
     * Gets the {@link RefinedSelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser refinedSelectorParser() {
        return refinedSelector;
    }

    /**
     * Gets the {@link SimpleSelectorSequenceParser}.
     * 
     * @return The parser instance.
     */
    public static Parser simpleSelectorSequenceParser() {
        return simpleSelectorSequence;
    }

    /**
     * Gets a parser to parse the start of a simple selector.
     * 
     * @return The parser instance.
     */
    public static Parser simpleSelectorStartParser() {
        return simpleSelectorStart;
    }

    /**
     * Gets the parser to parse a {@link SimpleSelector}.
     * 
     * @return The parser instance.
     */
    public static Parser simpleSelectorParser() {
        // FIXME add pseudo element
        return simpleSelector;
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
     * Gets the {@link PseudoClassSelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser pseudoClassSelectorParser() {
        return pseudoClassSelector;
    }

    /**
     * Gets the {@link PseudoElementSelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser psuedoElementSelectorParser() {
        return pseudoElementSelector;
    }

    /**
     * Gets the {@link NegationSelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser negationSelectorParser() {
        return negationSelector;
    }

    /**
     * Gets the {@link RawDeclarationParser}.
     * 
     * @return The parser instance.
     */
    public static Parser rawDeclarationParser() {
        return rawDeclaration;
    }

    /**
     * Gets the {@link TermListParser}.
     * 
     * @return The parser instance.
     */
    public static Parser termListParser() {
        return termList;
    }

    /**
     * Gets the parser to parse a {@link Term} value.
     * 
     * @return The parser instance.
     */
    public static Parser termParser() {
        return term;
    }
}
