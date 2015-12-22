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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.ast.declaration.RawFunction;
import com.salesforce.omakase.broadcast.Broadcaster;

/**
 * Refines {@link RawFunction}s to {@link LinearGradientFunctionValue}s.
 *
 * @author nmcwilliams
 * @see LinearGradientFunctionValue
 */
public final class LinearGradientRefiner implements FunctionRefiner {
    private static final String NORMAL = "linear-gradient";
    private static final String REPEATING = "repeating-linear-gradient";

    @Override
    public Refinement refine(RawFunction raw, Broadcaster broadcaster, MasterRefiner refiner) {
        if (raw.name().equals(NORMAL)) {
            broadcaster.broadcast(new LinearGradientFunctionValue(raw.line(), raw.column(), raw.args()));
            return Refinement.FULL;
        }
        if (raw.name().equals(REPEATING)) {
            broadcaster.broadcast(new LinearGradientFunctionValue(raw.line(), raw.column(), raw.args()).repeating(true));
            return Refinement.FULL;
        }
        return Refinement.NONE;
    }
}
