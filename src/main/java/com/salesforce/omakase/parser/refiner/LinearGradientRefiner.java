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
    public boolean refine(RawFunction raw, Broadcaster broadcaster, GenericRefiner refiner) {
        if (raw.name().equals(NORMAL)) {
            broadcaster.broadcast(new LinearGradientFunctionValue(raw.line(), raw.column(), raw.args()));
            return true;
        }
        if (raw.name().equals(REPEATING)) {
            broadcaster.broadcast(new LinearGradientFunctionValue(raw.line(), raw.column(), raw.args()).repeating(true));
            return true;
        }
        return false;
    }
}
