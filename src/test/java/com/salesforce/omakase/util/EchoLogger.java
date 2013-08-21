/**
 * ADD LICENSE
 */
package com.salesforce.omakase.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.value.*;
import com.salesforce.omakase.ast.selector.*;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.plugin.BasePlugin;

/**
 * Simply logs the creation or change of {@link Syntax} units. Used for debugging.
 * 
 * <p>
 * Most events are logged at {@link Level#INFO}, however some events are {@link Level#TRACE} or {@link Level#DEBUG}.
 * Update the logging config file as appropriate to filter which levels are shown in the console.
 * 
 * @author nmcwilliams
 */
public final class EchoLogger extends BasePlugin {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    @Override
    @Subscribe(priority = 1)
    public void syntax(Syntax syntax) {
        logger.trace("syntax: {}", syntax);
    }

    @Override
    @Subscribe(priority = 3)
    public void refinable(Refinable<?> refinable) {
        logger.trace("refinable: {}", refinable);
    }

    @Override
    @Subscribe(priority = 4)
    public void stylesheet(Stylesheet stylesheet) {
        logger.debug("stylesheet: {}", stylesheet);
    }

    @Override
    @Subscribe(priority = 5)
    public void statement(Statement statement) {
        logger.trace("statement: {}", statement);
    }

    @Override
    @Subscribe(priority = 6)
    public void rule(Rule rule) {
        logger.debug("rule: {}", rule);
    }

    @Override
    @Subscribe(priority = 8)
    public void selector(Selector selector) {
        logger.info("selector: {}", selector);
    }

    @Override
    @Subscribe(priority = 9)
    public void selectorPart(SelectorPart selectorPart) {
        logger.trace("selectorPart: {}", selectorPart);
    }

    @Override
    @Subscribe(priority = 9)
    public void simpleSelector(SimpleSelector simpleSelector) {
        logger.trace("simpleSelector: {}", simpleSelector);
    }

    @Override
    @Subscribe(priority = 10)
    public void combinator(Combinator combinator) {
        logger.info("combinator: {}", combinator);
    }

    @Override
    @Subscribe(priority = 11)
    public void typeSelector(TypeSelector typeSelector) {
        logger.info("typeSelector: {}", typeSelector);
    }

    @Override
    @Subscribe(priority = 12)
    public void idSelector(IdSelector idSelector) {
        logger.info("idSelector: {}", idSelector);
    }

    @Override
    @Subscribe(priority = 13)
    public void classSelector(ClassSelector classSelector) {
        logger.info("classSelector: {}", classSelector);
    }

    @Override
    @Subscribe(priority = 14)
    public void attributeSelector(AttributeSelector attributeSelector) {
        logger.info("attributeSelector: {}", attributeSelector);
    }

    @Override
    @Subscribe(priority = 15)
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {
        logger.info("pseudoClassSelector: {}", pseudoClassSelector);
    }

    @Override
    @Subscribe(priority = 16)
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {
        logger.info("pseudoElementSelector: {}", pseudoElementSelector);
    }

    @Override
    @Subscribe(priority = 17)
    public void universalSelector(UniversalSelector universalSelector) {
        logger.info("universalSelector: {}", universalSelector);
    }

    @Override
    @Subscribe(priority = 18)
    public void declaration(Declaration declaration) {
        logger.info("declaration: {}", declaration);
    }

    @Override
    @Subscribe(priority = 20)
    public void propertyValue(PropertyValue propertyValue) {
        logger.trace("propertyValue: {}", propertyValue);
    }

    @Override
    @Subscribe(priority = 20)
    public void term(Term term) {
        logger.trace("term: {}", term);
    }

    @Override
    @Subscribe(priority = 20)
    public void termList(TermList termList) {
        logger.info("termList: {}", termList);
    }

    @Override
    @Subscribe(priority = 20)
    public void functionValue(FunctionValue functionValue) {
        logger.trace("functionValue: {}", functionValue);
    }

    @Override
    @Subscribe(priority = 20)
    public void hexColorValue(HexColorValue hexColorValue) {
        logger.trace("hexColorValue: {}", hexColorValue);
    }

    @Override
    @Subscribe(priority = 20)
    public void keywordValue(KeywordValue keywordValue) {
        logger.trace("keywordValue: {}", keywordValue);
    }

    @Override
    @Subscribe(priority = 20)
    public void numericalValue(NumericalValue numericalValue) {
        logger.trace("numericalValue: {}", numericalValue);
    }

    @Override
    @Subscribe(priority = 20)
    public void stringValue(StringValue stringValue) {
        logger.trace("stringValue: {}", stringValue);
    }
}
