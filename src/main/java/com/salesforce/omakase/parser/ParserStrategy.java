/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.declaration.Property;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.parser.declaration.TermListParser;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public final class ParserStrategy {

    /**
     * TODO Description
     * 
     * @param propertyName
     *            TODO
     * @return TODOs
     */
    public static Parser getValueParser(PropertyName propertyName) {
        if (propertyName instanceof Property) {
            Property property = (Property)propertyName;

            switch (property) {
            default:
                return new TermListParser();
            }
        }

        return new TermListParser();
    }
}
