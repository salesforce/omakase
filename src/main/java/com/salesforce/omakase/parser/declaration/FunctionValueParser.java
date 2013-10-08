/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.parser.declaration;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.value.FunctionValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.Source.Snapshot;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link FunctionValue}.
 * <p/>
 * This does not validate the arguments inside of the parenthesis, but only that the the opening and closing parenthesis are
 * matched.
 *
 * @author nmcwilliams
 * @see FunctionValue
 */
public class FunctionValueParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // snapshot the current state before parsing
        Snapshot snapshot = source.snapshot();

        // read the function name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) return false;

        // must be an open parenthesis
        if (!Tokens.OPEN_PAREN.matches(source.current())) return snapshot.rollback();

        // read the arguments. This behavior itself differs from the spec a little. We aren't validating what's inside
        // the arguments. The more specifically typed function values will be responsible for validating their own args.
        String args = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);

        FunctionValue value = new FunctionValue(snapshot.originalLine, snapshot.originalColumn, name.get(), args);
        value.comments(source.flushComments());
        broadcaster.broadcast(value);

        return true;
    }

}
