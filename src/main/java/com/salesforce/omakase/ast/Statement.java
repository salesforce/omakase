/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

import static com.salesforce.omakase.emitter.SubscribableRequirement.SYNTAX_TREE;

/**
 * TESTME
 * A top-level {@link Syntax} unit, for example a {@link Rule} or {@link AtRule}.
 * <p/>
 * Note that {@link Statement}s are not be created unless the {@link SyntaxTree} plugin is enabled.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "rule or at-rule", broadcasted = SYNTAX_TREE)
public interface Statement extends Syntax, Groupable<Statement> {
}
