/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * A hex color value (e.g., "fffeee").
 * 
 * @author nmcwilliams
 */
public class HexColorValue implements Term {
    private String color;

    /**
     * Constructs a new instance of a {@link HexColorValue}.
     * 
     * @param color
     *            The hex color (do not include the #).
     */
    public HexColorValue(String color) {
        this.color = color;
    }

    /**
     * Sets the value of the color
     * 
     * @param color
     *            The hex color (do not include the #).
     * @return this, for chaining.
     */
    public HexColorValue color(String color) {
        this.color = checkNotNull(color, "color cannot be null");
        return this;
    }

    /**
     * Gets the color value (does not include the #).
     * 
     * @return The color value.
     */
    public String color() {
        return color;
    }

    /**
     * Gets whether this hex color is shorthand (has a length of three).
     * 
     * @return True if the length of this color is 3.
     */
    public boolean isShorthand() {
        return color.length() == 3;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("color", color)
            .toString();
    }
}
