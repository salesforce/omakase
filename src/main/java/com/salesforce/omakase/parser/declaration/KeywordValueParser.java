/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.value.KeywordValue;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses a {@link KeywordValue}.
 * 
 * @author nmcwilliams
 */
public class KeywordValueParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        int line = stream.line();
        int column = stream.column();

        Optional<String> keyword = stream.readIdent();
        if (!keyword.isPresent()) return false;

        KeywordValue value = new KeywordValue(line, column, keyword.get());

        broadcaster.broadcast(value);
        return true;
    }

}
