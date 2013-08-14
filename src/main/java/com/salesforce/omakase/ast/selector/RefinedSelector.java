/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.ast.Syntax;

/**
 * A refined {@link Selector}, with the selector's individual parts fully parsed.
 * 
 * @author nmcwilliams
 */
public interface RefinedSelector extends Syntax {
    /**
     * Gets the individual parts of the selector.
     * 
     * @return The list of {@link SelectorPart} members.
     */
    LinkableCollection<SelectorPart> parts();
}
