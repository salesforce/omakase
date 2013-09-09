/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.broadcaster.Broadcaster;

/**
 * A plugin that needs access to a {@link Broadcaster}.
 * <p/>
 * In most cases this is not the type of plugin that you want for customized (non-library-provided) plugins. Broadcasting your own
 * events may result in unexpected behavior.
 *
 * @author nmcwilliams
 */
public interface BroadcastingPlugin extends Plugin {
    /**
     * This method will be called with a reference to the {@link Broadcaster} for the current parsing operation.
     *
     * @param broadcaster
     *     The {@link Broadcaster} instance.
     */
    void broadcaster(Broadcaster broadcaster);
}
