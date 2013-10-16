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

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link HexColorValue}.
 *
 * @author nmcwilliams
 * @see HexColorValue
 */
public final class HexColorValueParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // grab current position before parsing
        int line = source.originalLine();
        int column = source.originalColumn();

        // starts with hash and then a valid hex character
        if (Tokens.HASH.matches(source.current()) && Tokens.HEX_COLOR.matches(source.peek())) {
            // skip the has mark
            source.next();

            // get the color value
            String color = source.chomp(Tokens.HEX_COLOR);

            // check for a valid length
            if (color.length() != 6 && color.length() != 3) throw new ParserException(source, Message.INVALID_HEX, color);

            HexColorValue value = new HexColorValue(line, column, color);
            value.comments(source.flushComments());

            broadcaster.broadcast(value);
            return true;
        }

        return false;
    }

}
