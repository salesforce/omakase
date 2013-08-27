/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.EmittableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * A simple selector, as defined by the Selectors Level 3 spec: "A simple selector is either a type selector, universal
 * selector, attribute selector, class selector, ID selector, or pseudo-class."
 * 
 * <p>
 * Note that a {@link PseudoElementSelector} is not a simple selector.
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "parent interface for simple selectors", broadcasted = REFINED_SELECTOR)
public interface SimpleSelector extends SelectorPart {
}
