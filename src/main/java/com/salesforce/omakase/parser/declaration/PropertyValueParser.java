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

package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Parses a {@link PropertyValue}.
 *
 * @author nmcwilliams
 * @see PropertyValue
 */
public final class PropertyValueParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        source.skipWhitepace();

        // grab the line and column number before parsing anything
        int line = source.originalLine();
        int column = source.originalColumn();

        // parse terms and operators
        QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster);
        ParserFactory.termSequenceParser().parse(source, qb, refiner);

        // if no terms were parsed then return false
        if (qb.count() == 0) return false;

        // create the term list and add the members
        PropertyValue value = new PropertyValue(line, column, broadcaster);
        value.members().appendAll(qb.filter(PropertyValueMember.class));

        // check for !important
        value.important(ParserFactory.importantParser().parse(source, broadcaster, refiner));

        // broadcast the new term list. we set propagate as true to allow for custom functions that did not broadcast
        // their terms during refinement (because it is not desired for the parsed terms to be directly added to this term list)
        // to have their inner terms broadcasted now.
        broadcaster.broadcast(value, true);

        return true;
    }
}
