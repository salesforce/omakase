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

import com.salesforce.omakase.ast.declaration.value.StringValue;
import com.salesforce.omakase.ast.declaration.value.StringValue.QuotationMode;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link StringValue}.
 *
 * @author nmcwilliams
 * @see StringValue
 */
public class StringValueParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        QuotationMode mode;
        String value;

        if (Tokens.SINGLE_QUOTE.matches(stream.current()) && !stream.isEscaped()) {
            mode = QuotationMode.SINGLE;
            value = stream.chompEnclosedValue(Tokens.SINGLE_QUOTE, Tokens.SINGLE_QUOTE);
        } else if (Tokens.DOUBLE_QUOTE.matches(stream.current()) && !stream.isEscaped()) {
            mode = QuotationMode.DOUBLE;
            value = stream.chompEnclosedValue(Tokens.DOUBLE_QUOTE, Tokens.DOUBLE_QUOTE);
        } else {
            return false;
        }

        StringValue string = new StringValue(snapshot.line, snapshot.column, mode, value);
        string.comments(stream.flushComments());
        broadcaster.broadcast(string);

        return true;
    }
}
