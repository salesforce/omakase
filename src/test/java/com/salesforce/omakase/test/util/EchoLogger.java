/*
 * Copyright (c) 2015, salesforce.com, inc.
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

package com.salesforce.omakase.test.util;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.declaration.*;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.ast.selector.*;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.plugin.BasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simply logs the creation or change of {@link Syntax} units. Used for debugging.
 * <p>
 * Events are logged at different levels. Update the logging config file as appropriate to filter which levels are shown in the
 * console.
 *
 * @author nmcwilliams
 */
public final class EchoLogger extends BasePlugin {
    private static final Logger logger = LoggerFactory.getLogger(EchoLogger.class);

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
        logger.debug("selector: {}", selector);
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
    public void typeSelector(TypeSelector typeSelector) {
        logger.debug("typeSelector: {}", typeSelector);
    }

    @Observe
    @Override
    public void idSelector(IdSelector idSelector) {
        logger.debug("idSelector: {}", idSelector);
    }

    @Observe
    @Override
    public void classSelector(ClassSelector classSelector) {
        logger.debug("classSelector: {}", classSelector);
    }

    @Observe
    @Override
    public void attributeSelector(AttributeSelector attributeSelector) {
        logger.debug("attributeSelector: {}", attributeSelector);
    }

    @Observe
    @Override
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {
        logger.debug("pseudoClassSelector: {}", pseudoClassSelector);
    }

    @Observe
    @Override
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {
        logger.debug("pseudoElementSelector: {}", pseudoElementSelector);
    }

    @Observe
    @Override
    public void universalSelector(UniversalSelector universalSelector) {
        logger.debug("universalSelector: {}", universalSelector);
    }

    @Observe
    @Override
    public void declaration(Declaration declaration) {
        logger.debug("declaration: {}", declaration);
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
    public void genericFunction(GenericFunctionValue function) {
        logger.trace("genericFunction: {}", function);
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

    @Observe
    @Override
    public void unquotedIEFilter(UnquotedIEFilter filter) {
        logger.trace("unquotedIEFilter: {}", filter);
    }

    @Observe
    @Override
    public void conditionalAtRuleBlock(ConditionalAtRuleBlock block) {
        logger.trace("conditionalAtRuleBlock: {}", block);
    }

    @Observe
    @Override
    public void urlValue(UrlFunctionValue url) {
        logger.trace("url: {}", url);
    }
}
