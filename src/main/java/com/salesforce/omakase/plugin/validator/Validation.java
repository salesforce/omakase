/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin.validator;

import java.util.List;

import com.google.common.collect.Lists;
import com.salesforce.omakase.plugin.Plugin;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class Validation {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static final List<Plugin> normal() {
        return Lists.<Plugin>newArrayList(new PseudoElementValidator());
    }
}
