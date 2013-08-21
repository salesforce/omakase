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
 * The value of a property in a {@link Declaration}.
 * 
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "interface for all property values", broadcasted = REFINED_DECLARATION)
public interface PropertyValue extends Syntax {
}
