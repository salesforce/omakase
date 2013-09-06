/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.declaration.value;

import static com.salesforce.omakase.emitter.SubscribableRequirement.REFINED_DECLARATION;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * A {@link TermListMember} within a {@link TermList} representing a single segment of the {@link Declaration} value.
 *
 * For example, in <code>margin: 3px 5px</code>, there are two terms, <code>3px</code> and <code>5px</code>.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "a single segment of a property value", broadcasted = REFINED_DECLARATION)
public interface Term extends TermListMember, Syntax {
}
