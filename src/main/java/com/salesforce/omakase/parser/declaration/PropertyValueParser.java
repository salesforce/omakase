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

package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.PropertyValueMember;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.QueryableBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;

/**
 * Parses a {@link PropertyValue}.
 *
 * @author nmcwilliams
 * @see PropertyValue
 */
public final class PropertyValueParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
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
