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

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Parses a {@link Rule}.
 *
 * @author nmcwilliams
 * @see Rule
 */
public final class RawRuleParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        source.collectComments();

        // save off current line and column
        int line = source.originalLine();
        int column = source.originalColumn();

        // wrap the broadcaster inside a queryable so we can gather the selectors and declarations
        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // if there isn't a selector then we aren't a rule
        if (!ParserFactory.rawSelectorSequenceParser().parse(source, queryable, refiner)) return false;

        // parse the declaration block
        source.skipWhitepace().expect(refiner.tokenFactory().declarationBlockBegin());

        // parse all declarations
        ParserFactory.rawDeclarationSequenceParser().parse(source, queryable, refiner);

        // create the rule and add selectors and declarations
        Rule rule = new Rule(line, column, broadcaster);
        rule.selectors().appendAll(queryable.filter(Selector.class));
        rule.declarations().appendAll(queryable.filter(Declaration.class));

        // add orphaned comments e.g., ".class{color:red; /*orphaned*/}"
        rule.orphanedComments(source.collectComments().flushComments());

        // parse the end of the block (must be after orphaned comments parsing)
        source.expect(refiner.tokenFactory().declarationBlockEnd());

        // broadcast the rule
        broadcaster.broadcast(rule);
        return true;
    }
}
