/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

/**
 * Vendor prefixes.
 * <p/>
 * Example: {@code PropertyName.using(Property.BORDER_RADIUS).prefix(Prefix.WEBKIT)}
 *
 * @author nmcwilliams
 */
@SuppressWarnings("UnusedDeclaration")
public enum Prefix {
    /** Mozilla Firefox */
    MOZ("-moz-"),
    /** Webkit */
    WEBKIT("-webkit-"),
    /** Microsoft */
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
