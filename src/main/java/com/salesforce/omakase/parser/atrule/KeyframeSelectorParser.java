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
import com.salesforce.omakase.parser.refiner.MasterRefiner;

/**
 * Parses a single {@link KeyframeSelector} part.
 * <p>
 * Note: this parsers a single {@link Selector} as well, which contains the {@link KeyframeSelector} {@link SelectorPart}.
 *
 * @author nmcwilliams
 * @see KeyframeSelector
 */
public final class KeyframeSelectorParser extends AbstractParser {
    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
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
