/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

/**
 * The type of selector or combinator for a {@link SelectorPart}. Checking against {@link SelectorPart#type()} can be
 * useful in place of using <pre>instanceof</pre>.
 *
 * @author nmcwilliams
 */
public enum SelectorPartType {
    /** a universal selector */
    UNIVERSAL_SELECTOR,

    /** a type (aka element) selector */
    TYPE_SELECTOR,

    /** an id selector */
    ID_SELECTOR,

    /** a class selector */
    CLASS_SELECTOR,

    /** an attribute selector */
    ATTRIBUTE_SELECTOR,

    /** a pseudo class selector */
    PSEUDO_CLASS_SELECTOR,

    /** a pseudo element selector */
    PSEUDO_ELEMENT_SELECTOR,

    /** a descendant combinator */
    DESCENDANT_COMBINATOR,

    /** a child combinator */
    CHILD_COMBINATOR,

    /** an adjacent sibling combinator */
    ADJACENT_SIBLING_COMBINATOR,

    /** a general sibling combinator */
    GENERAL_SIBLING_COMBINATOR
}
