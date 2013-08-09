/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

/**
 * TODO Description
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
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser stylesheetParser() {
        return stylesheetParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser atRuleParser() {
        return atRuleParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser ruleParser() {
        return ruleParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser statementParser() {
        return statementParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser declarationParser() {
        return declarationParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser selectorGroupParser() {
        return selectorGroupParser;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static Parser selectorParser() {
        return selectorParser;
    }
}
