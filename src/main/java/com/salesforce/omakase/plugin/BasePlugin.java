/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.ast.*;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.selector.*;
import com.salesforce.omakase.emitter.Subscribe;

/**
 * An optional base {@link Plugin} that can be extended from or used to see which types of subscriptions are possible.
 * 
 * <p>
 * It is <em>not</em> recommended that you override each one of these methods. Note that some methods are more generic
 * subscriptions that will also be covered by their more specific counterparts. For example, a {@link ClassSelector}
 * will be sent to {@link #classSelector(ClassSelector)}, {@link #selectorPart(SelectorPart)} and
 * {@link #syntax(Syntax)}.
 * 
 * <p>
 * See the notes on {@link Plugin} about invocation order for subscriptions within the same class.
 * 
 * @author nmcwilliams
 */
@SuppressWarnings("unused")
public class BasePlugin implements Plugin {
    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Syntax}.
     * 
     * @param syntax
     *            The {@link Syntax} instance.
     */
    public void syntax(Syntax syntax) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Refinable}.
     * 
     * @param refinable
     *            The {@link Refinable} instance.
     */
    public void refinable(Refinable<?> refinable) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Stylesheet}.
     * 
     * @param stylesheet
     *            The {@link Stylesheet} instance.
     */
    public void stylesheet(Stylesheet stylesheet) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Statement}.
     * 
     * @param statement
     *            The {@link Statement} instance.
     */
    public void statement(Statement statement) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Rule}.
     * 
     * @param rule
     *            The {@link Rule} instance.
     */
    public void rule(Rule rule) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link SelectorGroup}.
     * 
     * @param selectorGroup
     *            The {@link SelectorGroup} instance.
     */
    public void selectorGroup(SelectorGroup selectorGroup) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Selector}.
     * 
     * @param selector
     *            The {@link Selector} instance.
     */
    public void selector(Selector selector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link SelectorPart} ({@link SimpleSelector}s or {@link Combinator}s).
     * 
     * @param selectorPart
     *            The {@link SelectorPart} instance.
     */
    public void selectorPart(SelectorPart selectorPart) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link SimpleSelector}.
     * 
     * @param simpleSelector
     *            The {@link SimpleSelector} instance.
     */
    public void simpleSelector(SimpleSelector simpleSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Combinator}.
     * 
     * @param combinator
     *            The {@link Combinator} instance.
     */
    public void combinator(Combinator combinator) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link TypeSelector}.
     * 
     * @param typeSelector
     *            The {@link TypeSelector} instance.
     */
    public void typeSelector(TypeSelector typeSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link UniversalSelector}.
     * 
     * @param universalSelector
     *            The {@link UniversalSelector} instance.
     */
    public void universalSelector(UniversalSelector universalSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link IdSelector}.
     * 
     * @param idSelector
     *            The {@link IdSelector} instance.
     */
    public void idSelector(IdSelector idSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link ClassSelector}.
     * 
     * @param classSelector
     *            The {@link ClassSelector} instance.
     */
    public void classSelector(ClassSelector classSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link AttributeSelector}.
     * 
     * @param attributeSelector
     *            The {@link AttributeSelector} instance.
     */
    public void attributeSelector(AttributeSelector attributeSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PseudoClassSelector}.
     * 
     * @param pseudoClassSelector
     *            The {@link PseudoClassSelector} instance.
     */
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PseudoElementSelector}.
     * 
     * @param pseudoElementSelector
     *            The {@link PseudoElementSelector} instance.
     */
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link Declaration}.
     * 
     * @param declaration
     *            The {@link Declaration} instance.
     */
    public void declaration(Declaration declaration) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PropertyName}.
     * 
     * @param propertyName
     *            The {@link PropertyName} instance.
     */
    public void propertyName(PropertyName propertyName) {}

    /**
     * Override this method and add the {@link Subscribe} annotation in order to receive events for {@link Syntax} units
     * of type {@link PropertyValue}.
     * 
     * @param propertyValue
     *            The {@link PropertyValue} instance.
     */
    public void propertyValue(PropertyValue propertyValue) {}
}