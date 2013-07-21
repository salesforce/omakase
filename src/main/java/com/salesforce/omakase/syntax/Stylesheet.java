/**
 * ADD LICENSE
 */
package com.salesforce.omakase.syntax;

import java.util.List;

import com.salesforce.omakase.syntax.impl.RefinedStylesheet;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface Stylesheet extends Refinable<RefinedStylesheet> {
    Stylesheet rule(Rule rule);

    /**
     * TODO Description
     * 
     * @return TODO
     */
    List<? extends Rule> rules();
}
