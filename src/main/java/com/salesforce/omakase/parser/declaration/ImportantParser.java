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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses "!important"s at the end of a declaration's property value.
 *
 * @author nmcwilliams
 * @see PropertyValue
 */
public final class ImportantParser extends AbstractParser {
    private static final String IMPORTANT = "important";

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
        source.skipWhitepace();

        if (!source.optionallyPresent(Tokens.EXCLAMATION)) return false;

        // spec says that there can be a comment between ! and the word important; not allowing this here
        Optional<String> ident = source.readIdent();
        if (!ident.isPresent() || !ident.get().equalsIgnoreCase(IMPORTANT)) {
            throw new ParserException(source, Message.EXPECTED_IMPORTANT);
        }

        return true;
    }
}
