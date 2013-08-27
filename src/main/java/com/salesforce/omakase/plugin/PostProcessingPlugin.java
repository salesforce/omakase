/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface PostProcessingPlugin extends Plugin {
    /**
     * This method will be called after source processing has ended (high-level parsing).
     * 
     * <p>
     * This is mainly used when the {@link Plugin} must defer it's processing until it is certain that all
     * {@link Selector}s and {@link Declaration}s within the source are processed. See the notes on {@link Plugin} for
     * more information.
     * 
     * <p>
     * The order in which this will be received is the same order that the {@link Plugin} was registered.
     * 
     * @param context
     *            The {@link Context} instance.
     */
    void after(Context context);
}
