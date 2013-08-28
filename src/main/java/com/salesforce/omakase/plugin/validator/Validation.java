/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.validator;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.plugin.Plugin;

/**
 * Standard library-provided validation plugins.
 * 
 * @author nmcwilliams
 */
public final class Validation {
    /**
     * Gets the list of normal validations. This should be used in almost all cases, unless you don't want any
     * validation at all.
     * 
     * @return The list of normal validations.
     */
    public static final List<Plugin> normal() {
        return Lists.<Plugin>newArrayList(new PseudoElementValidator());
    }
}
