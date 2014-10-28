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

package com.salesforce.omakase.parser.atrule;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.selector.KeyframeSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.GenericRefiner;

/**
 * Parses a single {@link KeyframeSelector} part.
 * <p/>
 * Note: this parsers a single {@link Selector} as well, which contains the {@link KeyframeSelector} {@link SelectorPart}.
 *
 * @author nmcwilliams
 * @see KeyframeSelector
 */
public final class KeyframeSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, GenericRefiner refiner) {
        source.skipWhitepace();

        int line = source.originalLine();
        int column = source.originalColumn();

        KeyframeSelector keyframeSelector = null;

        // first try a percentage
        SingleInterestBroadcaster<NumericalValue> b = SingleInterestBroadcaster.of(NumericalValue.class);
        ParserFactory.numericalValueParser().parse(source, b, refiner);

        if (b.broadcasted().isPresent()) {
            // must have the percentage sign
            NumericalValue numerical = b.broadcasted().get();
            if (!numerical.unit().isPresent() || !numerical.unit().get().equals("%")) {
                throw new ParserException(source, Message.MISSING_PERCENTAGE);
            }

            keyframeSelector = new KeyframeSelector(line, column, numerical.value() + numerical.unit().get());
        } else {
            // try keywords
            if (source.readConstant("from")) {
                keyframeSelector = new KeyframeSelector(line, column, "from");
            } else if (source.readConstant("to")) {
                keyframeSelector = new KeyframeSelector(line, column, "to");
            }
        }

        if (keyframeSelector == null) return false;

        // create and broadcast the parent selector
        Selector selector = new Selector(line, column, keyframeSelector);
        selector.propagateBroadcast(broadcaster); // propagate because the original broadcaster wasn't used to parse
        return true;
    }
}
