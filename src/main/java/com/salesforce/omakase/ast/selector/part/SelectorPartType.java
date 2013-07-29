/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector.part;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorPartType {
    public enum Selector {
        UNIVERSAL,
        TYPE,
        ID,
        CLASS,
        ATTRIBUTE,
        PSEUDO_ELEMENT,
        PSEUDO_CLASS
    }

    public enum Combinator {
        DESCENDENT,
        CHILD,
        ADJACENT_SIBLING,
        GENERAL_SIBLING
    }
}
