/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface SelectorPartType {
    /** TODO */
    public enum Selector {
        /** TODO */
        UNIVERSAL,
        /** TODO */
        TYPE,
        /** TODO */
        ID,
        /** TODO */
        CLASS,
        /** TODO */
        ATTRIBUTE,
        /** TODO */
        PSEUDO_ELEMENT,
        /** TODO */
        PSEUDO_CLASS
    }

    /** TODO */
    public enum Combinator {
        /** TODO */
        DESCENDENT,
        /** TODO */
        CHILD,
        /** TODO */
        ADJACENT_SIBLING,
        /** TODO */
        GENERAL_SIBLING
    }
}
