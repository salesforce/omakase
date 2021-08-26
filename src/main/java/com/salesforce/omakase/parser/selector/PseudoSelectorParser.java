/*
 * Copyright (c) 2015, salesforce.com, inc.
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

package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.ast.selector.SelectorPartType.PSEUDO_CLASS_SELECTOR;
import static com.salesforce.omakase.ast.selector.SelectorPartType.PSEUDO_ELEMENT_SELECTOR;

import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses both {@link PseudoClassSelector}s and {@link PseudoElementSelector}s.
 *
 * @author nmcwilliams
 */
public final class PseudoSelectorParser implements Parser {

    @Override
    public boolean parse(Source source, Grammar grammar, Broadcaster broadcaster) {
        // note: important not to skip whitespace, as it could skip over a descendant combinator
        source.collectComments(false);

        // grab current position before parsing
        int line = source.originalLine();
        int column = source.originalColumn();

        // first character must be a colon
        if (!source.optionallyPresent(Tokens.COLON)) return false;

        // one colon (already parsed above) equals pseudo class selector, two colons equals pseudo element selector
        SelectorPartType type = source.optionallyPresent(Tokens.COLON) ? PSEUDO_ELEMENT_SELECTOR : PSEUDO_CLASS_SELECTOR;

        // read the name
        Optional<String> name = source.readIdent();

        // name must be present
        if (!name.isPresent()) throw new ParserException(source, Message.MISSING_PSEUDO_NAME);

        // certain pseudo elements can still use pseudo class syntax
        if (PseudoElementSelector.POSERS.contains(name.get())) {
            type = PSEUDO_ELEMENT_SELECTOR;
        }

        Syntax selector;

        if (type == PSEUDO_ELEMENT_SELECTOR) {
            selector = new PseudoElementSelector(line, column, name.get());
        } else {
            // check for arguments (currently only applies to pseudo classes)
            String args = null;
            if (Tokens.OPEN_PAREN.matches(source.current())) {
                args = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN).trim();
            }

            selector = new PseudoClassSelector(line, column, name.get(), args);
        }

        selector.comments(source.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }

}
