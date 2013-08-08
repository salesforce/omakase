/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.Context;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 * @since 0.1
 */
public interface DependentPlugin extends Plugin {
    /**
     * TODO Description
     * 
     * @param context
     *            TODO
     */
    void before(Context context);
}
