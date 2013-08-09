/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import java.util.Iterator;

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
    Iterator<SelectorPart> parts();
}
