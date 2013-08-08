/**
 * ADD LICENSE
 */
package com.salesforce.omakase;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.emitter.SubscriptionType;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Broadcaster {
    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param type
     *            TODO
     * @param syntax
     *            TODO
     */
    public <T extends Syntax> void broadcast(SubscriptionType type, T syntax);
}
