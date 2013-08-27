/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import static com.salesforce.omakase.emitter.EmittableRequirement.REFINED_SELECTOR;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.Groupable;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Emittable;

/**
 * A {@link SimpleSelector}, {@link PseudoElementSelector}, or {@link Combinator}.
 * 
 * @author nmcwilliams
 */
@Emittable
@Description(value = "parent interface for all selector segments", broadcasted = REFINED_SELECTOR)
public interface SelectorPart extends Syntax, Groupable<SelectorPart> {

    /**
     * Gets whether this {@link SelectorPart} is a selector ({@link SimpleSelector} or {@link PseudoElementSelector}).
     * 
     * @return True if this {@link SelectorPart} is a selector.
     */
    boolean isSelector();

    /**
     * Gets whether this {@link SelectorPart} is a {@link Combinator}.
     * 
     * @return True if this {@link SelectorPart} is a {@link Combinator}.
     */
    boolean isCombinator();

    /**
     * Gets the {@link SelectorPartType} of this {@link SelectorPart}.
     * 
     * @return The {@link SelectorPartType}.
     */
    SelectorPartType type();
}
