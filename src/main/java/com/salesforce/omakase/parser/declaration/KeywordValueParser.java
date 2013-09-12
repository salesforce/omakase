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
 * @see KeywordValue
 */
public class KeywordValueParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        stream.collectComments();

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // read the keyword
        Optional<String> keyword = stream.readIdent();
        if (!keyword.isPresent()) return false;

        KeywordValue value = new KeywordValue(snapshot.line, snapshot.column, keyword.get());
        value.comments(stream.flushComments());
        broadcaster.broadcast(value);

        return true;
    }
}
