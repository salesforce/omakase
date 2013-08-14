/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.Property;
import com.salesforce.omakase.ast.PropertyName;

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
                return new GenericPropertyValueParser();
            }
        }

        return new GenericPropertyValueParser();
    }
}
