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

import com.salesforce.omakase.ast.Comment;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;

/**
 * Parses a {@link Rule}.
 *
 * @author nmcwilliams
 * @see Rule
 */
public final class RawRuleParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        source.skipWhitepace();
        source.collectComments();

        // save off current line and column
        int line = source.originalLine();
        int column = source.originalColumn();

        // wrap the broadcaster inside a queryable so we can gather the selectors and declarations
        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // if there isn't a selector then we aren't a rule
        if (!ParserFactory.selectorGroupParser().parse(source, queryable, refiner)) return false;

        // skip whitespace after selectors
        source.skipWhitepace();

        // parse the declaration block
        source.expect(tokenFactory().declarationBlockBegin());

        // parse all declarations
        do {
            source.skipWhitepace();
            ParserFactory.rawDeclarationParser().parse(source, queryable, refiner);
            source.skipWhitepace();
        } while (source.optionallyPresent(tokenFactory().declarationDelimiter()));

        // create the rule and add selectors and declarations
        Rule rule = new Rule(line, column, broadcaster);
        rule.selectors().appendAll(queryable.filter(Selector.class));
        rule.declarations().appendAll(queryable.filter(Declaration.class));

        // add orphaned comments e.g., ".class{color:red; /*orphaned*/}"
        for (String comment : source.collectComments().flushComments()) {
            rule.orphanedComment(new Comment(comment));
        }

        // parse the end of the block (must be after orphaned comments parsing)
        source.expect(tokenFactory().declarationBlockEnd());

        // broadcast the rule
        broadcaster.broadcast(rule);
        return true;
    }
}
