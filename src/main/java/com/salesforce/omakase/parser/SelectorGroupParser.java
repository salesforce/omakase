/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser;

import com.salesforce.omakase.Context;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class SelectorGroupParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Context context) {
        stream.until(Tokens.OPEN_BRACKET);
        return true;
    }

}
