/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;

/**
 * A {@link PropertyName} that is currently unknown to this library.
 * 
 * @author nmcwilliams
 */
public final class UnknownPropertyName implements PropertyName {
    private final String name;

    /**
     * Constructs a new {@link UnknownPropertyName} instance with the given name.
     * 
     * @param name
     *            The name of the property.
     */
    public UnknownPropertyName(String name) {
        this.name = checkNotNull(name, "name cannot be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return As.string(this).add("name", name).toString();
    }
}
