/**
 * ADD LICENSE
 */
package com.salesforce.omakase.emitter;

import com.salesforce.omakase.ast.Syntax;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public enum SubscriptionType {
    /** indicates that a {@link Syntax} node was created */
    CREATED,
    /** indicates that a {@link Syntax} node was updated */
    CHANGED
}
