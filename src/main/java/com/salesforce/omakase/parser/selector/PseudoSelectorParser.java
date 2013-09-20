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

package com.salesforce.omakase.parser.selector;

import com.google.common.base.Optional;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.SelectorPartType;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.token.Tokens;

import static com.salesforce.omakase.ast.selector.SelectorPartType.*;

/**
 * TESTME
 * <p/>
 * Parses both {@link PseudoClassSelector}s and {@link PseudoElementSelector}.
 *
 * @author nmcwilliams
 */
public class PseudoSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator
        stream.collectComments(false);

        // snapshot the current state before parsing
        Stream.Snapshot snapshot = stream.snapshot();

        // first character must be a colon
        if (!stream.optionallyPresent(Tokens.COLON)) return false;

        // one colon (already parsed above) equals pseudo class selector, two colons equals pseudo element selector
        SelectorPartType type = stream.optionallyPresent(Tokens.COLON) ? PSEUDO_ELEMENT_SELECTOR : PSEUDO_CLASS_SELECTOR;

        // read the name
        Optional<String> name = stream.readIdent();

        // name must be present
        if (!name.isPresent()) throw new ParserException(stream, Message.MISSING_PSEUDO_NAME);

        // certain pseudo elements can still use pseudo class syntax
        if (PseudoElementSelector.POSERS.contains(name.get())) {
            type = PSEUDO_ELEMENT_SELECTOR;
        }

        Syntax selector;

        if (type == PSEUDO_ELEMENT_SELECTOR) {
            selector = new PseudoElementSelector(snapshot.line, snapshot.column, name.get());
        } else {
            // check for arguments (currently only applies to pseudo classes)
            String args = null;
            if (Tokens.OPEN_PAREN.matches(stream.current())) {
                args = stream.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN).trim();
            }

            selector = new PseudoClassSelector(snapshot.line, snapshot.column, name.get(), args);
        }

        selector.comments(stream.flushComments());
        broadcaster.broadcast(selector);
        return true;
    }
}
