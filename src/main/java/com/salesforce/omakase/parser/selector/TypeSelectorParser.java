/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;

/**
 * Parses {@link TypeSelector}s.
 *
 * @author nmcwilliams
 * @see TypeSelector
 */
public class TypeSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // find the name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) return false;

        // create and broadcast the new selector
        TypeSelector selector = new TypeSelector(snapshot.line, snapshot.column, name.get());
        selector.comments(stream.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }

}
