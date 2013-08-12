/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

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
    private static final Parser stylesheetParser = new StylesheetParser();
    private static final Parser atRuleParser = new AtRuleParser();
    private static final Parser ruleParser = new RuleParser();
    private static final Parser statementParser = atRuleParser.or(ruleParser);
    private static final Parser declarationParser = new DeclarationParser();
    private static final Parser selectorGroupParser = new SelectorGroupParser();
    private static final Parser selectorParser = new SelectorParser();

    /**
     * Gets the {@link StylesheetParser}.
     * 
     * @return The parser instance.
     */
    public static Parser stylesheetParser() {
        return stylesheetParser;
    }

    /**
     * Gets the {@link AtRuleParser}.
     * 
     * @return The parser instance.
     */
    public static Parser atRuleParser() {
        return atRuleParser;
    }

    /**
     * Gets the {@link RuleParser}.
     * 
     * @return The parser instance.
     */
    public static Parser ruleParser() {
        return ruleParser;
    }

    /**
     * Gets the statement parser.
     * 
     * @return The parser instance.
     */
    public static Parser statementParser() {
        return statementParser;
    }

    /**
     * Gets the {@link DeclarationParser}.
     * 
     * @return The parser instance.
     */
    public static Parser declarationParser() {
        return declarationParser;
    }

    /**
     * Gets the {@link SelectorGroupParser}.
     * 
     * @return The parser instance.
     */
    public static Parser selectorGroupParser() {
        return selectorGroupParser;
    }

    /**
     * Gets the {@link SelectorParser}.
     * 
     * @return The parser instance.
     */
    public static Parser selectorParser() {
        return selectorParser;
    }
}
