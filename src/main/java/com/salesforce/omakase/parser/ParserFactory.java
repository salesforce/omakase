/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.parser.declaration.TermListParser;
import com.salesforce.omakase.parser.raw.AtRuleParser;
import com.salesforce.omakase.parser.raw.RawDeclarationParser;
import com.salesforce.omakase.parser.raw.RawSelectorParser;
import com.salesforce.omakase.parser.raw.RuleParser;
import com.salesforce.omakase.parser.raw.SelectorGroupParser;
import com.salesforce.omakase.parser.raw.StylesheetParser;
import com.salesforce.omakase.parser.selector.AttributeSelectorParser;
import com.salesforce.omakase.parser.selector.ClassSelectorParser;
import com.salesforce.omakase.parser.selector.CombinatorParser;
import com.salesforce.omakase.parser.selector.IdSelectorParser;
import com.salesforce.omakase.parser.selector.NegationSelectorParser;
import com.salesforce.omakase.parser.selector.PseudoClassSelectorParser;
import com.salesforce.omakase.parser.selector.PseudoElementSelectorParser;
import com.salesforce.omakase.parser.selector.RefinedSelectorParser;
import com.salesforce.omakase.parser.selector.SimpleSelectorSequenceParser;
import com.salesforce.omakase.parser.selector.TypeSelectorParser;
import com.salesforce.omakase.parser.selector.UniversalSelectorParser;

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
     * Gets a parser to parse a simple selector.
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
}
