/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.plugin.basic.AutoRefiner;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * A {@link Plugin} that have dependencies on other {@link Plugin}s.
 * 
 * @author nmcwilliams
 */
public interface DependentPlugin extends Plugin {
    /**
     * This method will be called just before source code processing begins.
     * 
     * <p>
     * The main purpose of this method is to allow you to specify a dependency on and/or configure another
     * {@link Plugin}. In many cases a dependency on {@link SyntaxTree} or {@link AutoRefiner} is required. See the
     * comments on {@link Plugin} for more details.
     * 
     * <p>
     * The order in which this will be invoked (between plugins) is the same order that the {@link Plugin} was
     * registered.
     * 
     * @see PluginRegistry#require(Class)
     * @see PluginRegistry#require(Class, com.google.common.base.Supplier)
     * @see PluginRegistry#retrieve(Class)
     * 
     * @param registry
     *            The {@link PluginRegistry} instance.
     */
    void dependencies(PluginRegistry registry);
}
