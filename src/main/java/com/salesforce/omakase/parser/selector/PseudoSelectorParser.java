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
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

import static com.salesforce.omakase.ast.selector.SelectorPartType.*;

/**
 * Parses both {@link PseudoClassSelector}s and {@link PseudoElementSelector}s.
 *
 * @author nmcwilliams
 */
public final class PseudoSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
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
