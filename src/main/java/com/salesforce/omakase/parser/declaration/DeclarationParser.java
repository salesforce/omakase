/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.ConsumingBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Token;

import java.util.Optional;

/**
 * Parses a {@link Declaration}.
 *
 * @author nmcwilliams
 * @see Declaration
 */
public final class DeclarationParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        source.collectComments();

        // grab our current position before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        // read optional 'special' character (e.g., * from a star hack)
        Optional<Token> specialToken = grammar.token().specialDeclarationBegin();
        Optional<Character> special = specialToken.flatMap(source::optional);

        // read the property name
        Optional<String> ident = source.readIdentLevel3();
        if (!ident.isPresent()) return false;

        String content = special.isPresent() ? special.get() + ident.get() : ident.get();
        RawSyntax property = new RawSyntax(line, column, content.trim());

        // read colon
        source.skipWhitepace();
        source.expect(grammar.token().propertyNameEnd(), Message.MISSING_COLON);
        source.skipWhitepace();

        //read the property value
        line = source.originalLine();
        column = source.originalColumn();
        content = source.until(grammar.token().declarationEnd());
        RawSyntax value = new RawSyntax(line, column, content.trim());

        // create the new declaration and associate comments
        Declaration declaration = new Declaration(property, value);
        declaration.comments(source.flushComments());

        // notifier listeners of the new declaration
        broadcaster.chainBroadcast(declaration,
            new ConsumingBroadcaster<>(PropertyValue.class, declaration::propertyValue));

        return true;
    }

}
