/**
 * ADD LICENSE
 */
package com.salesforce.omakase.plugin;

import com.salesforce.omakase.Context;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;

/**
 * A {@link Plugin} that is dependent on another {@link Plugin}, or for some other reason needs access to the
 * {@link Context}. The {@link Context} will be provided before and after high-level processing.
 * 
 * @author nmcwilliams
 */
public interface DependentPlugin extends Plugin {
    /**
     * This method will be called just before source processing begins.
     * 
     * <p>
     * The main purpose of this method is to allow you to specify a dependency on and/or configure another
     * {@link Plugin}. This is usually required when subscribing to a {@link Syntax} unit more specific than a
     * {@link Selector} or {@link Declaration}. See the comments on {@link Plugin} for more details.
     * 
     * <p>
     * You can also store the {@link Context} instance for later usage if you will need it.
     * 
     * <p>
     * The order in which this will be received is the same order that the {@link Plugin} was registered.
     * 
     * @param context
     *            The {@link Context} instance.
     */
    void before(Context context);

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
