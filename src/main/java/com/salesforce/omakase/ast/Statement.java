/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.salesforce.omakase.emitter.EmittableRequirement.SYNTAX_TREE;

import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * A top-level {@link Syntax} unit, for example a {@link Rule} or {@link AtRule}.
 * 
 * <p>
 * Note that {@link Statement}s are not be created unless the {@link SyntaxTree} plugin is enabled.
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "rule or at-rule", broadcasted = SYNTAX_TREE)
public interface Statement extends Syntax, Groupable<Statement> {
}
