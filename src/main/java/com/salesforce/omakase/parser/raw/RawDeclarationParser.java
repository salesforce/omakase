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
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link Declaration}.
 *
 * @author nmcwilliams
 * @see Declaration
 */
public final class RawDeclarationParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.collectComments();

        // grab our current position before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        // the IE7 star hack - http://en.wikipedia.org/wiki/CSS_filter#Star_hack - is not part of the CSS spec,
        // but it still needs to be handled
        Optional<Character> starHack = source.optional(Tokens.STAR);

        // read the property name
        Optional<String> ident = source.readIdent();
        if (!ident.isPresent()) return false;

        String content = starHack.isPresent() ? starHack.get() + ident.get() : ident.get();
        RawSyntax property = new RawSyntax(line, column, content.trim());

        // read colon
        source.skipWhitepace();
        source.expect(tokenFactory().propertyNameEnd(), Message.MISSING_COLON);
        source.skipWhitepace();

        //read the property value
        line = source.originalLine();
        column = source.originalColumn();
        content = source.until(tokenFactory().declarationEnd());
        RawSyntax value = new RawSyntax(line, column, content.trim());

        // create the new declaration and associate comments
        Declaration declaration = new Declaration(property, value, refiner);
        declaration.comments(source.flushComments());

        // notifier listeners of the new declaration
        broadcaster.broadcast(declaration);
        return true;
    }
}
