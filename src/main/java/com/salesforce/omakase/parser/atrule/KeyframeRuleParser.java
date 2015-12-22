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

package com.salesforce.omakase.parser.atrule;

import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.raw.RawRuleParser;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Similar to {@link RawRuleParser}, except this only parses {@link KeyframeSelector}s.
 *
 * @author nmcwilliams
 */
public final class KeyframeRuleParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        source.collectComments();

        int line = source.originalLine();
        int column = source.originalColumn();

        // wrap the broadcaster inside a queryable so we can gather the selectors and declarations
        QueryableBroadcaster queryable = new QueryableBroadcaster(broadcaster);

        // if there isn't a selector then we aren't a rule
        if (!ParserFactory.keyframeSelectorSequenceParser().parse(source, queryable, refiner)) return false;

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
