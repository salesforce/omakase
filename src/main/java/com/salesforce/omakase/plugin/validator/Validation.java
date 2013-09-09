/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.validator;

import com.google.common.collect.Lists;
import com.salesforce.omakase.plugin.Plugin;

import java.util.List;

/**
 * Standard library-provided validation plugins.
 *
 * @author nmcwilliams
 */
public final class Validation {
    /** do not construct */
    private Validation() {}

    /**
     * Gets the list of normal validations. This should be used in almost all cases, unless you don't want any validation at all.
     *
     * @return The list of normal validations.
     */
    public static List<Plugin> normal() {
        return Lists.<Plugin>newArrayList(new PseudoElementValidator());
    }
}
