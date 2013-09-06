/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Plugin} that wishes to be notified when all processing is completed.
 *
 * @author nmcwilliams
 */
public interface PostProcessingPlugin extends Plugin {
    /**
     * This method will be called after all processing has completed (preprocessing, rework, and validation).
     *
     * This could be used when the {@link Plugin} must defer it's processing until it is certain that all
     * {@link Selector}s and {@link Declaration}s within the source are processed.
     *
     * The order in which this will be invoked (between plugins) is the same order that the {@link Plugin} was
     * registered.
     *
     * @param registry
     *            The {@link PluginRegistry} instance.
     */
    void postProcess(PluginRegistry registry);
}
