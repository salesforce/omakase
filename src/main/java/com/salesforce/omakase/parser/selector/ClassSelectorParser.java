/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link ClassSelector}.
 *
 * @author nmcwilliams
 * @see ClassSelector
 */
public class ClassSelectorParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // first character must be a dot
        if (!stream.optionallyPresent(Tokens.DOT)) return snapshot.rollback();

        // parse the class name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) throw new ParserException(stream, Message.EXPECTED_VALID_CLASS);

        // broadcast the new class selector
        ClassSelector selector = new ClassSelector(snapshot.line, snapshot.column, name.get());
        selector.comments(stream.flushComments());
        broadcaster.broadcast(selector);

        return true;
    }

}
