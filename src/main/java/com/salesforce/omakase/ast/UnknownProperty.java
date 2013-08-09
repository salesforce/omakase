/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class UnknownProperty implements Property {
    private final String name;

    /**
     * TODO
     * 
     * @param name
     *            TODO
     */
    public UnknownProperty(String name) {
        this.name = checkNotNull(name, "name cannot be null");
    }

    @Override
    public String propertyName() {
        return name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).toString();
    }
}
