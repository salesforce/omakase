/**
 * ADD LICENSE
 */
package com.salesforce.omakase.parser.selector;

import static com.salesforce.omakase.ast.selector.SelectorPartType.PSEUDO_CLASS_SELECTOR;
import static com.salesforce.omakase.ast.selector.SelectorPartType.PSEUDO_ELEMENT_SELECTOR;

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

/**
 * Parses both {@link PseudoClassSelector}s and {@link PseudoElementSelector}.
 * 
 * @author nmcwilliams
 */
public class PseudoSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Stream stream, Broadcaster broadcaster) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a descendant combinator

        // gather the line and column before advancing the stream
        int line = stream.line();
        int column = stream.column();

        // first character must be a colon
        if (!stream.optionallyPresent(Tokens.COLON)) return false;

        // 1 colon (already parsed above) equals pseudo class selector, 2 colons equals pseudo element selector
        SelectorPartType type = stream.optionallyPresent(Tokens.COLON) ? PSEUDO_ELEMENT_SELECTOR : PSEUDO_CLASS_SELECTOR;

        // read the name
        Optional<String> name = stream.readIdent();

        // name must be present
        if (!name.isPresent()) throw new ParserException(stream, Message.MISSING_PSEUDO_NAME);

        // certain pseudo elements can still use pseudo class syntax
        if (PseudoElementSelector.POSERS.contains(name.get())) {
            type = PSEUDO_ELEMENT_SELECTOR;
        }

        // TODO pseudos with functions

        // create the selector and broadcast it
        Syntax selector = (type == PSEUDO_CLASS_SELECTOR) ?
                new PseudoClassSelector(line, column, name.get()) :
                new PseudoElementSelector(line, column, name.get());

        broadcaster.broadcast(selector);
        return true;
    }
}
