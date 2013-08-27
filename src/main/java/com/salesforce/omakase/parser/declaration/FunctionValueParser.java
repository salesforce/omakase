/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.emitter.SubscriptionType;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.Stream.Snapshot;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link FunctionValue}.
 * 
 * <p>
 * This does not validate the arguments inside of the parenthesis, but only that the the opening and closing parenthesis
 * are matched.
 * 
 * @author nmcwilliams
 */
public class FunctionValueParser extends AbstractParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // save state
        Snapshot snapshot = stream.snapshot();

        // read the function name
        Optional<String> name = stream.readIdent();
        if (!name.isPresent()) return stream.rollback();

        // must be an open parenthesis
        if (!Tokens.OPEN_PAREN.matches(stream.current())) return stream.rollback();

        // read the arguments. This behavior itself differs from the spec a little. We aren't validating what's inside
        // the arguments. The more specifically typed function values will be responsible for validating their own args.
        String args = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);

        FunctionValue value = new FunctionValue(snapshot.line, snapshot.column, name.get(), args);
        broadcaster.broadcast(SubscriptionType.CREATED, value);

        return true;
    }
}
