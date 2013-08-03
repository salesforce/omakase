/**
 * ADD LICENSE
 */
package com.salesforce.omakase.consumer;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SyntaxValidator implements Consumer {
    @Override
    public void selectorGroup(SelectorGroup selectorGroup) {
        selectorGroup.refine();
    }

    @Override
    public void declaration(Declaration declaration) {
        declaration.refine();
    }
}
