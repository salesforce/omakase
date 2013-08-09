/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.emitter.Subscribe;

/**
 * An optional base {@link Plugin} that can be extended from or used to see which types of subscriptions are possible.
 * 
 * <p> It is <em>not</em> recommended that you override each one of these methods. Note that some methods are more
 * generic subscriptions that will also be covered by their more specific counterparts. For example, a
 * {@link ClassSelector} will be sent to {@link #classSelector(ClassSelector)}, {@link #selectorPart(SelectorPart)} and
 * {@link #syntax(Syntax)}.
 * 
 * <p> See the notes on {@link Plugin} about invocation order for subscriptions within the same class.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings({ "unused" })
public class BasePlugin implements Plugin {
    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Syntax}.
     * 
     * @param syntax
     *            TODO
     */
    public void syntax(Syntax syntax) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link RawSyntax}.
     * 
     * @param rawSyntax
     *            TODO
     */
    public void rawSyntax(RawSyntax rawSyntax) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Refinable}.
     * 
     * @param refinable
     *            TODO
     */
    public void refinable(Refinable<?> refinable) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Stylesheet}.
     * 
     * @param stylesheet
     *            TODO
     */
    public void stylesheet(Stylesheet stylesheet) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Statement}.
     * 
     * @param statement
     *            TODO
     */
    public void statement(Statement statement) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Rule}.
     * 
     * @param rule
     *            TODO
     */
    public void rule(Rule rule) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link SelectorGroup}.
     * 
     * @param selectorGroup
     *            TODO
     */
    public void selectorGroup(SelectorGroup selectorGroup) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Selector}.
     * 
     * @param selector
     *            TODO
     */
    public void selector(Selector selector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link SelectorPart}.
     * 
     * @param selectorPart
     *            TODO
     */
    public void selectorPart(SelectorPart selectorPart) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Combinator}.
     * 
     * @param combinator
     *            TODO
     */
    public void combinator(Combinator combinator) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link TypeSelector}.
     * 
     * @param typeSelector
     *            TODO
     */
    public void typeSelector(TypeSelector typeSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link IdSelector}.
     * 
     * @param idSelector
     *            TODO
     */
    public void idSelector(IdSelector idSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link ClassSelector}.
     * 
     * @param classSelector
     *            TODO
     */
    public void classSelector(ClassSelector classSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link AttributeSelector}.
     * 
     * @param attributeSelector
     *            TODO
     */
    public void attributeSelector(AttributeSelector attributeSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PseudoClassSelector}.
     * 
     * @param pseudoClassSelector
     *            TODO
     */
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PseudoElementSelector}.
     * 
     * @param pseudoElementSelector
     *            TODO
     */
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link UniversalSelector}.
     * 
     * @param universalSelector
     *            TODO
     */
    public void universalSelector(UniversalSelector universalSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Declaration}.
     * 
     * @param declaration
     *            TODO
     */
    public void declaration(Declaration declaration) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PropertyName}.
     * 
     * @param propertyName
     *            TODO
     */
    public void propertyName(PropertyName propertyName) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PropertyValue}.
     * 
     * @param propertyValue
     *            TODO
     */
    public void propertyValue(PropertyValue propertyValue) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link NumericValue}.
     * 
     * @param numericValue
     *            TODO
     */
    public void numericValue(NumericValue numericValue) {}
}
