/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum Prefix {
    /** TODO */
    MOZ("-moz-"),
    /** TODO */
    WEBKIT("-webkit-"),
    /** TODO */
    MS("-ms-");

    private final String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
