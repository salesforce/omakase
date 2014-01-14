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

package com.salesforce.omakase.parser.raw;

import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;

/**
 * Parses a sequence of semi-colon delimited {@link Declaration}s.
 *
 * @author nmcwilliams
 */
public class RawDeclarationSequenceParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
        boolean parsed = false;

        do {
            if (ParserFactory.rawDeclarationParser().parse(source.skipWhitepace(), broadcaster, refiner)) parsed = true;
        } while (source.skipWhitepace().optionallyPresent(tokenFactory().declarationDelimiter()));

        return parsed;
    }
}
