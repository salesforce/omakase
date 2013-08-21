/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;

/**
 * A refined {@link Selector}, with the selector's individual parts fully parsed.
 * 
 * @see ComplexSelectorParser
 * 
 * @author nmcwilliams
 */
public interface RefinedSelector extends Syntax {
    /**
     * Gets the individual parts of the selector.
     * 
     * @return The list of {@link SelectorPart} members.
     */
    SyntaxCollection<SelectorPart> parts();
}
