/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class HexColorValue implements ExpressionTerm {
    private String color;

    /**
     * TODO
     * 
     * @param color
     *            TODO
     */
    public HexColorValue(String color) {
        this.color = color;
    }

    /**
     * TODO Description
     * 
     * @param color
     *            TODO
     * @return TODO
     */
    public HexColorValue color(String color) {
        this.color = checkNotNull(color, "color cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public String color() {
        return color;
    }

    /**
     * TODO Description
     * 
     * @return TODO
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
