/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.builder;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public interface StylesheetBuilder extends Builder<Stylesheet> {
    /**
     * TODO Description
     * 
     * @param statement
     *            TODO
     * @return TODO
     */
    StylesheetBuilder statement(Statement statement);

    /**
     * TODO Description
     * 
     * @param rule
     *            TODO
     * @return TODO
     */
    StylesheetBuilder rule(Rule rule);

}
