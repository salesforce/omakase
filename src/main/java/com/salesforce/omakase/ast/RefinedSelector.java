/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.salesforce.omakase.LinkableCollection;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface RefinedSelector extends Syntax {
    /**
     * TODO Description
     * 
     * @return TODO
     */
    LinkableCollection<SelectorPart> parts();
}
