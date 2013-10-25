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

package com.salesforce.omakase.plugin;

import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.ast.declaration.*;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.ast.selector.*;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.plugin.basic.Conditionals;
import com.salesforce.omakase.plugin.other.UnquotedIEFilterPlugin;

/**
 * An optional base {@link Plugin} that can be extended from or used to see which types of subscriptions are possible.
 * <p/>
 * It is <em>not</em> recommended that you override each one of these methods. Note that some methods are more generic
 * subscriptions that will also be covered by their more specific counterparts. For example, a {@link ClassSelector} will be sent
 * to {@link #classSelector(ClassSelector)}, {@link #selectorPart(SelectorPart)} and {@link #syntax(Syntax)}.
 * <p/>
 * See the notes on {@link Plugin} about invocation order for subscription methods.
 *
 * @author nmcwilliams
 */
@SuppressWarnings("UnusedParameters")
public class BasePlugin implements Plugin {
    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Syntax}.
     *
     * @param syntax
     *     The {@link Syntax} instance.
     */
    public void syntax(Syntax syntax) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Refinable}.
     *
     * @param refinable
     *     The {@link Refinable} instance.
     */
    public void refinable(Refinable<?> refinable) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Stylesheet}.
     *
     * @param stylesheet
     *     The {@link Stylesheet} instance.
     */
    public void stylesheet(Stylesheet stylesheet) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Statement}.
     *
     * @param statement
     *     The {@link Statement} instance.
     */
    public void statement(Statement statement) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Rule}.
     *
     * @param rule
     *     The {@link Rule} instance.
     */
    public void rule(Rule rule) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link AtRule}.
     *
     * @param rule
     *     The {@link AtRule} instance.
     */
    public void atRule(AtRule rule) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Selector}.
     *
     * @param selector
     *     The {@link Selector} instance.
     */
    public void selector(Selector selector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link SelectorPart} ({@link SimpleSelector}s or {@link Combinator}s).
     *
     * @param selectorPart
     *     The {@link SelectorPart} instance.
     */
    public void selectorPart(SelectorPart selectorPart) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link SimpleSelector}.
     *
     * @param simpleSelector
     *     The {@link SimpleSelector} instance.
     */
    public void simpleSelector(SimpleSelector simpleSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link TypeSelector}.
     *
     * @param typeSelector
     *     The {@link TypeSelector} instance.
     */
    public void typeSelector(TypeSelector typeSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UniversalSelector}.
     *
     * @param universalSelector
     *     The {@link UniversalSelector} instance.
     */
    public void universalSelector(UniversalSelector universalSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link IdSelector}.
     *
     * @param idSelector
     *     The {@link IdSelector} instance.
     */
    public void idSelector(IdSelector idSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link ClassSelector}.
     *
     * @param classSelector
     *     The {@link ClassSelector} instance.
     */
    public void classSelector(ClassSelector classSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link AttributeSelector}.
     *
     * @param attributeSelector
     *     The {@link AttributeSelector} instance.
     */
    public void attributeSelector(AttributeSelector attributeSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PseudoClassSelector}.
     *
     * @param pseudoClassSelector
     *     The {@link PseudoClassSelector} instance.
     */
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PseudoElementSelector}.
     *
     * @param pseudoElementSelector
     *     The {@link PseudoElementSelector} instance.
     */
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Declaration}.
     *
     * @param declaration
     *     The {@link Declaration} instance.
     */
    public void declaration(Declaration declaration) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PropertyValue}.
     *
     * @param propertyValue
     *     The {@link PropertyValue} instance.
     */
    public void propertyValue(PropertyValue propertyValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Term}.
     *
     * @param term
     *     The {@link Term} instance.
     */
    public void term(Term term) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link TermList}.
     *
     * @param termList
     *     The {@link TermList} instance.
     */
    public void termList(TermList termList) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link RawFunction}.
     *
     * @param raw
     *     The {@link RawFunction} instance.
     */
    public void rawFunction(RawFunction raw) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link GenericFunctionValue}.
     *
     * @param function
     *     The {@link GenericFunctionValue} instance.
     */
    public void genericFunction(GenericFunctionValue function) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UrlFunctionValue}.
     *
     * @param url
     *     The {@link UrlFunctionValue} instance.
     */
    public void urlValue(UrlFunctionValue url) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link HexColorValue}.
     *
     * @param hexColorValue
     *     The {@link HexColorValue} instance.
     */
    public void hexColorValue(HexColorValue hexColorValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link KeywordValue}.
     *
     * @param keywordValue
     *     The {@link KeywordValue} instance.
     */
    public void keywordValue(KeywordValue keywordValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link NumericalValue}.
     *
     * @param numericalValue
     *     The {@link NumericalValue} instance.
     */
    public void numericalValue(NumericalValue numericalValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link StringValue}.
     *
     * @param stringValue
     *     The {@link StringValue} instance.
     */
    public void stringValue(StringValue stringValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UnquotedIEFilter} (only enabled with the {@link UnquotedIEFilterPlugin} plugin.
     *
     * @param filter
     *     The {@link UnquotedIEFilter} instance.
     */
    public void unquotedIEFilter(UnquotedIEFilter filter) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link ConditionalAtRuleBlock} (only enabled with the {@link Conditionals} plugin.
     *
     * @param block
     *     The {@link ConditionalAtRuleBlock} instance.
     */
    public void conditionalAtRuleBlock(ConditionalAtRuleBlock block) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link MediaQueryList}.
     *
     * @param list
     *     The {@link MediaQueryList} instance.
     */
    public void mediaQueryList(MediaQueryList list) {}
}
