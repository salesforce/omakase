/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.Property;
import com.salesforce.omakase.ast.declaration.PropertyName;
import com.salesforce.omakase.parser.declaration.TermListParser;

/**
 * TESTME Helper for getting an appropriate {@link Parser} for a given {@link PropertyName}.
 * 
 * @author nmcwilliams
 */
public final class ParserStrategy {
    /** do not construct */
    private ParserStrategy() {}

    /**
     * Gets the appropriate parser for the given property value. By default this will fallback to the
     * {@link TermListParser}.
     * 
     * @param propertyName
     *            The {@link Declaration}'s property name.
     * @return The parser instance.
     */
    public static Parser getValueParser(PropertyName propertyName) {
        if (propertyName instanceof Property) {
            Property property = (Property)propertyName;

            switch (property) {
            default:
                return ParserFactory.termListParser();
            }
        }

        return ParserFactory.termListParser();
    }
}
