/**
 * ADD LICENSE
 */
package com.salesforce.omakase.observer;

import com.salesforce.omakase.ast.Declaration;
import com.salesforce.omakase.ast.selector.SelectorGroup;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Validator implements Observer {
    @Override
    public void comment(String comment) {}

    @Override
    public void selectorGroup(SelectorGroup selectors) {
        selectors.refine();
    }

    @Override
    public void declaration(Declaration declaration) {
        declaration.refine();
    }
}
