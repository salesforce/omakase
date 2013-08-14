/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.parser.token.IdentSequence;
import com.salesforce.omakase.parser.token.TokenSequence;

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
    private static final Parser declaration = new RawDeclarationParser();
    private static final Parser selectorGroup = new SelectorGroupParser();
    private static final Parser selector = new RawSelectorParser();
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

    /* sequences */
    private static final TokenSequence ident = new IdentSequence();

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
    public static Parser selectorParser() {
        return selector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser refinedSelectorParser() {
        return refinedSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser simpleSelectorSequenceParser() {
        return simpleSelectorSequence;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser simpleSelectorStartParser() {
        return simpleSelectorStart;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser simpleSelectorParser() {
        return simpleSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser combinatorParser() {
        return combinator;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser classSelectorParser() {
        return classSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser idSelectorParser() {
        return idSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser attributeSelectorParser() {
        return attributeSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser typeSelectorParser() {
        return typeSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser universalSelectorParser() {
        return universalSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser pseudoClassSelectorParser() {
        return pseudoClassSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser psuedoElementSelectorParser() {
        return pseudoElementSelector;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser negationParser() {
        return negationSelector;
    }

    /**
     * Gets the {@link RawDeclarationParser}.
     * 
     * @return The parser instance.
     */
    public static Parser declarationParser() {
        return declaration;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static TokenSequence ident() {
        return ident;
    }
}
