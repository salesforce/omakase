/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.Subscribable;

/**
 * A top-level {@link Syntax} unit, for example a {@link Rule} or AtRule.
 * 
 * @author nmcwilliams
 */
@Subscribable
public interface Statement extends Syntax, Linkable<Statement> {
}
