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

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.Source.Snapshot;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link GenericFunctionValue}.
 * <p>
 * This does not validate the arguments inside of the parenthesis, but only that the the opening and closing parenthesis are
 * matched.
 *
 * @author nmcwilliams
 * @see GenericFunctionValue
 */
public final class FunctionValueParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // snapshot the current state before parsing
        Snapshot snapshot = source.snapshot();

        // read the function name
        Optional<String> name = source.readIdent();
        if (!name.isPresent()) return false;

        // must be an open parenthesis
        if (!Tokens.OPEN_PAREN.matches(source.current())) return snapshot.rollback();

        // read the arguments. We aren't validating what's inside the arguments. The more specifically typed function values
        // will be responsible for validating their own args.
        String args = source.chompEnclosedValue(Tokens.OPEN_PAREN, Tokens.CLOSE_PAREN);

        // create the intermediary raw function
        RawFunction raw = new RawFunction(snapshot.originalLine, snapshot.originalColumn, name.get(), args);
        raw.comments(source.flushComments());

        // we are broadcasting this to allow for plugins to modify the raw args before it gets to the refiners. However
        // RawFunction is NOT an instance of Term and will not be stored or maintained by the PropertyValue itself. It's
        // essentially discarded after the refiners deal with it.
        broadcaster.broadcast(raw);

        // a refiner will broadcast the actual function term
        refiner.refine(raw, broadcaster);

        return true;
    }

}
