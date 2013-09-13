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

package com.salesforce.omakase.test.util;

import ch.qos.logback.classic.Level;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.value.*;
import com.salesforce.omakase.ast.selector.*;
import com.salesforce.omakase.emitter.Observe;
import com.salesforce.omakase.plugin.BasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simply logs the creation or change of {@link Syntax} units. Used for debugging.
 * <p/>
 * Most events are logged at {@link Level#INFO}, however some events are {@link Level#TRACE} or {@link Level#DEBUG}. Update the
 * logging config file as appropriate to filter which levels are shown in the console.
 *
 * @author nmcwilliams
 */
public final class EchoLogger extends BasePlugin {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

    @Observe
    @Override
    public void syntax(Syntax syntax) {
        logger.trace("syntax: {}", syntax);
    }

    @Observe
    @Override
    public void refinable(Refinable<?> refinable) {
        logger.trace("refinable: {}", refinable);
    }

    @Observe
    @Override
    public void stylesheet(Stylesheet stylesheet) {
        logger.debug("stylesheet: {}", stylesheet);
    }

    @Observe
    @Override
    public void statement(Statement statement) {
        logger.trace("statement: {}", statement);
    }

    @Observe
    @Override
    public void rule(Rule rule) {
        logger.debug("rule: {}", rule);
    }

    @Observe
    @Override
    public void atRule(AtRule rule) {
        logger.debug("at-rule: {}", rule);
    }

    @Observe
    @Override
    public void selector(Selector selector) {
        logger.info("selector: {}", selector);
    }

    @Observe
    @Override
    public void selectorPart(SelectorPart selectorPart) {
        logger.trace("selectorPart: {}", selectorPart);
    }

    @Observe
    @Override
    public void simpleSelector(SimpleSelector simpleSelector) {
        logger.trace("simpleSelector: {}", simpleSelector);
    }

    @Observe
    @Override
    public void combinator(Combinator combinator) {
        logger.info("combinator: {}", combinator);
    }

    @Observe
    @Override
    public void typeSelector(TypeSelector typeSelector) {
        logger.info("typeSelector: {}", typeSelector);
    }

    @Observe
    @Override
    public void idSelector(IdSelector idSelector) {
        logger.info("idSelector: {}", idSelector);
    }

    @Observe
    @Override
    public void classSelector(ClassSelector classSelector) {
        logger.info("classSelector: {}", classSelector);
    }

    @Observe
    @Override
    public void attributeSelector(AttributeSelector attributeSelector) {
        logger.info("attributeSelector: {}", attributeSelector);
    }

    @Observe
    @Override
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {
        logger.info("pseudoClassSelector: {}", pseudoClassSelector);
    }

    @Observe
    @Override
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {
        logger.info("pseudoElementSelector: {}", pseudoElementSelector);
    }

    @Observe
    @Override
    public void universalSelector(UniversalSelector universalSelector) {
        logger.info("universalSelector: {}", universalSelector);
    }

    @Observe
    @Override
    public void declaration(Declaration declaration) {
        logger.info("declaration: {}", declaration);
    }

    @Observe
    @Override
    public void propertyValue(PropertyValue propertyValue) {
        logger.trace("propertyValue: {}", propertyValue);
    }

    @Observe
    @Override
    public void term(Term term) {
        logger.trace("term: {}", term);
    }

    @Observe
    @Override
    public void termList(TermList termList) {
        logger.info("termList: {}", termList);
    }

    @Observe
    @Override
    public void functionValue(FunctionValue functionValue) {
        logger.trace("functionValue: {}", functionValue);
    }

    @Observe
    @Override
    public void hexColorValue(HexColorValue hexColorValue) {
        logger.trace("hexColorValue: {}", hexColorValue);
    }

    @Observe
    @Override
    public void keywordValue(KeywordValue keywordValue) {
        logger.trace("keywordValue: {}", keywordValue);
    }

    @Observe
    @Override
    public void numericalValue(NumericalValue numericalValue) {
        logger.trace("numericalValue: {}", numericalValue);
    }

    @Observe
    @Override
    public void stringValue(StringValue stringValue) {
        logger.trace("stringValue: {}", stringValue);
    }
}
