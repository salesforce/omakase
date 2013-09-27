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

package com.salesforce.omakase.parser;

import com.salesforce.omakase.parser.token.StandardTokenFactory;
import com.salesforce.omakase.parser.token.Token;
import com.salesforce.omakase.parser.token.TokenFactory;

/**
 * Base class for {@link Parser}s.
 *
 * @author nmcwilliams
 */
public abstract class AbstractParser implements Parser {
    /**
     * Utility method to create a {@link CombinationParser} comprised of this and the given {@link Parser}.
     *
     * @param other
     *     The {@link Parser} to use.
     *
     * @return A new {@link CombinationParser} instance.
     */
    @Override
    public Parser or(Parser other) {
        return new CombinationParser(this, other);
    }

    /**
     * Gets the {@link TokenFactory} to use for various {@link Token} delimiters.
     *
     * @return The factory.
     */
    protected TokenFactory tokenFactory() {
        // static instance for now, but later we might want this to be customizable
        return StandardTokenFactory.instance();
    }
}
