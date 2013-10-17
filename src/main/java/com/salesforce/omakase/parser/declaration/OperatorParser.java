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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.Operator;
import com.salesforce.omakase.ast.declaration.OperatorType;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.Refiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a single term {@link Operator}.
 *
 * @author nmcwilliams
 * @see Operator
 */
public class OperatorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, Refiner refiner) {
        // skip comments at the beginning but not whitespace
        source.collectComments(false);

        // save off the current position before parsing any content
        int line = source.originalLine();
        int column = source.originalColumn();

        // The presence of a space *could* be the "single space" term operator.
        // Or it could just be whitespace around another term operator.
        boolean mightBeSpaceOperator = source.optionallyPresent(Tokens.WHITESPACE);

        // after we've already checked for the single space operator, it's ok to consume comments
        // and surrounding whitespace.
        source.collectComments();

        // see if there is an actual non-space operator
        Optional<OperatorType> type = source.optionalFromEnum(OperatorType.class);

        // if no operator is parsed and we parsed at least one space then we know it's a single space operator
        if (mightBeSpaceOperator && !type.isPresent()) {
            type = Optional.of(OperatorType.SPACE);
        }

        // we didn't parse any operators
        if (!type.isPresent()) return false;

        // skip whitespace now that we know it can't be another space operator.
        source.skipWhitepace();

        // broadcast the parsed operator
        Operator operator = new Operator(line, column, type.get());
        broadcaster.broadcast(operator);
        return true;
    }
}
