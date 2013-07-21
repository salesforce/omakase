/**
 * ADD LICENSE
 */
package com.salesforce.omakase.observer;

import com.salesforce.omakase.syntax.Declaration;
import com.salesforce.omakase.syntax.Selector;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class Validator implements Observer {
    @Override
    public void comment(String comment) {}

    @Override
    public void selector(Selector selector) {
        selector.refine();
    }

    @Override
    public void declaration(Declaration declaration) {
        declaration.refine();
    }
}
