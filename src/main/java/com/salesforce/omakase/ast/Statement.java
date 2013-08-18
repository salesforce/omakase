/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.standard.SyntaxTree;

/**
 * A top-level {@link Syntax} unit, for example a {@link Rule} or AtRule.
 * 
 * <p>
 * Note that {@link Statement}s are not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Subscribable
public interface Statement extends Syntax, Linkable<Statement> {
}
