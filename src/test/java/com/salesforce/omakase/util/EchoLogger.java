/**
 * ADD LICENSE
 */
package com.salesforce.omakase.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.emitter.Subscribe;
import com.salesforce.omakase.plugin.BasePlugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class EchoLogger extends BasePlugin {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    @Override
    @Subscribe(priority = 1)
    public void syntax(Syntax syntax) {
        // logger.info("\nsyntax :{}", syntax);
    }

    @Override
    @Subscribe(priority = 2)
    public void rawSyntax(RawSyntax rawSyntax) {
        // logger.info("\nrawSyntax: {}", rawSyntax);
    }

    @Override
    @Subscribe(priority = 3)
    public void refinable(Refinable<?> refinable) {
        // logger.info("\nrefinable: {}", refinable);
    }

    @Override
    @Subscribe(priority = 4)
    public void stylesheet(Stylesheet stylesheet) {
        logger.info("\nstylesheet: {}", stylesheet);
    }

    @Override
    @Subscribe(priority = 5)
    public void statement(Statement statement) {
        logger.info("\nstatement: {}", statement);
    }

    @Override
    @Subscribe(priority = 6)
    public void rule(Rule rule) {
        logger.info("\nrule: {}", rule);
    }

    @Override
    @Subscribe(priority = 7)
    public void selectorGroup(SelectorGroup selectorGroup) {
        logger.info("\nselectorGroup: {}", selectorGroup);
    }

    @Override
    @Subscribe(priority = 8)
    public void selector(Selector selector) {
        logger.info("\nselector: {}", selector);
    }

    @Override
    @Subscribe(priority = 9)
    public void selectorPart(SelectorPart selectorPart) {
        logger.info("\nselectorPart: {}", selectorPart);
    }

    @Override
    @Subscribe(priority = 10)
    public void combinator(Combinator combinator) {
        logger.info("\ncombinator: {}", combinator);
    }

    @Override
    @Subscribe(priority = 11)
    public void typeSelector(TypeSelector typeSelector) {
        logger.info("\ntypeSelector: {}", typeSelector);
    }

    @Override
    @Subscribe(priority = 12)
    public void idSelector(IdSelector idSelector) {
        logger.info("\nidSelector: {}", idSelector);
    }

    @Override
    @Subscribe(priority = 13)
    public void classSelector(ClassSelector classSelector) {
        logger.info("\nclassSelector: {}", classSelector);
    }

    @Override
    @Subscribe(priority = 14)
    public void attributeSelector(AttributeSelector attributeSelector) {
        logger.info("\nattributeSelector: {}", attributeSelector);
    }

    @Override
    @Subscribe(priority = 15)
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {
        logger.info("\npseudoClassSelector: {}", pseudoClassSelector);
    }

    @Override
    @Subscribe(priority = 16)
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {
        logger.info("\npseudoElementSelector: {}", pseudoElementSelector);
    }

    @Override
    @Subscribe(priority = 17)
    public void universalSelector(UniversalSelector universalSelector) {
        logger.info("\nuniversalSelector: {}", universalSelector);
    }

    @Override
    @Subscribe(priority = 18)
    public void declaration(Declaration declaration) {
        logger.info("\ndeclaration: {}", declaration);
    }

    @Override
    @Subscribe(priority = 19)
    public void propertyName(PropertyName propertyName) {
        logger.info("\npropertyName: {}", propertyName);
    }

    @Override
    @Subscribe(priority = 20)
    public void propertyValue(PropertyValue propertyValue) {
        logger.info("\npropertyValue: {}", propertyValue);
    }

    @Override
    @Subscribe(priority = 21)
    public void numericValue(NumericValue numericValue) {
        logger.info("\nnumericValue: {}", numericValue);
    }
}
