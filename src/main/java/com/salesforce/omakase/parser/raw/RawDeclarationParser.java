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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractRefinableParser;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link Declaration}.
 *
 * @author nmcwilliams
 * @see Declaration
 */
public class RawDeclarationParser extends AbstractRefinableParser {

    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster, Refiner refiner) {
        stream.skipWhitepace();
        stream.collectComments();

        // the IE7 star hack - http://en.wikipedia.org/wiki/CSS_filter#Star_hack -
        // is not part of the CSS spec, but it still needs to be handled
        Stream.Snapshot snapshot = stream.snapshot();
        Optional<Character> starHack = stream.optional(Tokens.STAR);

        // the first non comment or space character must match the beginning of a declaration
        if (!tokenFactory().declarationBegin().matches(stream.current())) return snapshot.rollback();

        // get the property, which is everything up to the delimiter
        int line = stream.line();
        int column = stream.column();

        String content = stream.until(tokenFactory().propertyNameEnd());

        if (starHack.isPresent()) {
            // if we found the star hack, we need to write it back out
            content = starHack.get() + content;
        }

        RawSyntax property = new RawSyntax(line, column, content.trim());

        stream.skipWhitepace();
        stream.expect(tokenFactory().propertyNameEnd());
        stream.skipWhitepace();

        // get the value, which is everything until the end of the declaration
        line = stream.line();
        column = stream.column();
        content = stream.until(tokenFactory().declarationEnd());
        RawSyntax value = new RawSyntax(line, column, content.trim());

        // create the new declaration and associate comments
        Declaration declaration = new Declaration(property, value, refiner);
        declaration.comments(stream.flushComments());

        // notifier listeners of the new declaration
        broadcaster.broadcast(declaration);
        return true;
    }

}
